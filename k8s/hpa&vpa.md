# HPA和VPA

## HPA

Pod 水平自动扩缩（Horizontal Pod Autoscaler） 可以基于 CPU 利用率自动扩缩 ReplicationController、Deployment、ReplicaSet 和 StatefulSet 中的 Pod 数量。 除了 CPU 利用率，也可以基于其他应程序提供的 [自定义度量指标](https://git.k8s.io/community/contributors/design-proposals/instrumentation/custom-metrics-api.md) 来执行自动扩缩。 Pod 自动扩缩不适用于无法扩缩的对象，比如 DaemonSet。

### 工作机制

![image-20211125095542754](https://gitee.com/huowolf/pic-md/raw/master/image-20211125095542754.png)

### 相关kubectl 命令

* kubectl get hpa : 获取HPA对象

* kubectl describe hpa : 查看HPA对象的详细信息

* kubectl delete hpa：删除HPA对象。

* kubectl autoscale : 创建HPA对象

  例：kubectl autoscale rs foo --min=2 --max=5 --cpu-percent=80

  会为名 为 *foo* 的 ReplicationSet 创建一个 HPA 对象， 目标 CPU 使用率为 `80%`，副本数量配置为 2 到 5 之间。

## HPA实战

因为pod状态的采集都依赖于metrics-server，所以要首先安装metrics-server。

### 安装metrics-server

官方：https://github.com/kubernetes-sigs/metrics-server

```
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

```

执行完，查看pod状态，发觉pod无法就绪，查看pod日志，有如下报错

![image-20211124115147518](https://gitee.com/huowolf/pic-md/raw/master/image-20211124115147518.png)

解决方案：修改yaml文件，跳过证书验证，增加容器启动参数 `--kubelet-insecure-tls`

![image-20211125102212744](https://gitee.com/huowolf/pic-md/raw/master/image-20211125102212744.png)

重新apply yaml，pod正常就绪。

验证metrics-server正常运行，

kubectl top node

kubectl top pod

正常输出了node和pod的采集信息。

![image-20211125102904002](https://gitee.com/huowolf/pic-md/raw/master/image-20211125102904002.png)

### 创建演示的deployment并暴露服务

php-apache.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: php-apache
spec:
  selector:
    matchLabels:
      run: php-apache
  replicas: 1
  template:
    metadata:
      labels:
        run: php-apache
    spec:
      containers:
      - name: php-apache
        image: k8s.gcr.io/hpa-example
        ports:
        - containerPort: 80
        resources:
          limits:
            cpu: 500m
          requests:
            cpu: 200m

---

apiVersion: v1
kind: Service
metadata:
  name: php-apache
  labels:
    run: php-apache
spec:
  ports:
  - port: 80
  selector:
    run: php-apache

```

运行

```bash
kubectl apply -f php-apache.yaml
```

### 创建HPA

```bash
kubectl autoscale deployment php-apache --cpu-percent=50 --min=1 --max=5
```

查看HPA状态：

![image-20211125102739880](https://gitee.com/huowolf/pic-md/raw/master/image-20211125102739880.png)

当前的pod的cpu利用率为0%，如果metrics-server没有正常就绪，Targets这里cpu的当前利用率会显示为UNKOWN。

### 增加负载

```bash
kubectl run -i --tty load-generator --rm --image=busybox --restart=Never -- /bin/sh -c "while sleep 0.01; do wget -q -O- http://php-apache; done"
```

![image-20211125103840789](https://gitee.com/huowolf/pic-md/raw/master/image-20211125103840789.png)

查看hpa，发觉CPU负载升高了。

![image-20211125103955718](https://gitee.com/huowolf/pic-md/raw/master/image-20211125103955718.png)

查看Deployment的副本数，已经增长到最大值5。

![image-20211125104136178](https://gitee.com/huowolf/pic-md/raw/master/image-20211125104136178.png)

### 停止负载

停止负载以后，等待几分钟后，观察HPA状态和deploy状态。



## 参考

1、https://kubernetes.io/zh/docs/tasks/run-application/horizontal-pod-autoscale/

2、https://kubernetes.io/zh/docs/tasks/run-application/horizontal-pod-autoscale-walkthrough/

## VPA

官网：https://github.com/kubernetes/autoscaler/tree/master/vertical-pod-autoscaler

Vertical Pod Autoscaler（VPA）使用户无需为其pods中的容器设置最新的资源request。配置后，它将根据使用情况自动设置request，从而允许在节点上进行适当的调度，以便为每个pod提供适当的资源量。

### VPA实战

### 安装VPA

```bash
git clone https://github.com/kubernetes/autoscaler.git
cd vertical-pod-autoscaler/
./hack/vpa-up.sh
```

这里有个报错，`unknown option -addext`

参考以下文章，升级openssl到1.1.1即可。

https://www.cnblogs.com/itbsl/p/11275728.html

### 创建测试Deployment和VPA

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment-basic
  labels:
    app: nginx
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.7.9
        ports:
        - containerPort: 80
---
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: nginx-deployment-basic-vpa
spec:
  targetRef:
    apiVersion: "apps/v1"
    kind:       Deployment
    name:       nginx-deployment-basic
  updatePolicy:
    updateMode: "Off"
```

### 创建service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx
  namespace: vpa
spec:
  type: NodePort
  ports:
  - port: 80
    targetPort: 80
    nodePort: 30000
  selector:
    app: nginx
```

### 安装ab压测工具，对pod进行压测

```bash
yum install -y httpd-tools
```

压测：

```bash
ab -c 100 -n 1000000 http://192.168.209.129:30000/
```

压测前 ，查看vpa对象

```bash
kubectl  describe  vpa 
```

![image-20211124171507524](https://gitee.com/huowolf/pic-md/raw/master/image-20211124171507524.png)

压测后，观察vpa对象

![image-20211124172353987](https://gitee.com/huowolf/pic-md/raw/master/image-20211124172353987.png)

### 说明

VPA是处于试验版本中，生产不可用。

### 参考

1、https://help.aliyun.com/document_detail/173702.html

2、https://blog.csdn.net/weixin_41989934/article/details/118645084

