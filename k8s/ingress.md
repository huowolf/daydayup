# ingress

## ingress简介

[Ingress](https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.23/#ingress-v1beta1-networking-k8s-io) 公开了从集群外部到集群内[服务](https://kubernetes.io/zh/docs/concepts/services-networking/service/)的 HTTP 和 HTTPS 路由。 流量路由由 Ingress 资源上定义的规则控制。

下面是一个将所有流量都发送到同一 Service 的简单 Ingress 示例：

![image-20211210174248813](https://gitee.com/huowolf/pic-md/raw/master/image-20211210174248813.png)

## 安装Nginx Ingress Controller

#### 1、获取源码文件

```bash
$ git clone https://github.com/nginxinc/kubernetes-ingress/
$ cd kubernetes-ingress/deployments
$ git checkout v2.0.3
```

#### 2、配置RBAC

```bash
$ kubectl apply -f common/ns-and-sa.yaml
$ kubectl apply -f rbac/rbac.yaml
```

#### 3、创建自定义资源

3.1 创建secret和configmap

```bash
$ kubectl apply -f common/default-server-secret.yaml
$ kubectl apply -f common/nginx-config.yaml
```

注：当没有匹配的Ingress规则时，默认返回404状态。

3.2 创建IngressClass

配置nginx ingress controller作为默认的ingress控制器

```bash
vim common/ingress-class.yaml
```

取消对应的注释。

![image-20211209174406095](https://gitee.com/huowolf/pic-md/raw/master/image-20211209174406095.png)

```bash
$ kubectl apply -f common/ingress-class.yaml
```

3.3 创建CRD

```bash
$ kubectl apply -f common/crds/k8s.nginx.org_virtualservers.yaml
$ kubectl apply -f common/crds/k8s.nginx.org_virtualserverroutes.yaml
$ kubectl apply -f common/crds/k8s.nginx.org_transportservers.yaml
$ kubectl apply -f common/crds/k8s.nginx.org_policies.yaml
```

#### 4、以DaemonSet方式部署Ingress Controller

```bash
kubectl apply -f daemon-set/nginx-ingress.yaml
```

#### 5、访问Ingress Controller

DaemonSet部署的Ingress Controller，80端口和443端口默认已经通过hostport方式暴露到主机上了。

#### 6、卸载Ingress Controller【可选】

```bash
$ kubectl delete namespace nginx-ingress
$ kubectl delete clusterrole nginx-ingress
$ kubectl delete clusterrolebinding nginx-ingress
$ kubectl delete -f common/crds/
```

## 创建用于演示的nginx和tomcat服务以及对应的svc

vim nginx-deploy-svc.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deploy
  labels:
    app: nginx-deploy
spec:
  replicas: 1
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
        imagePullPolicy: IfNotPresent
        image: nginx
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: ClusterIP
  selector:
    app: nginx
  ports:
    - port: 4200
      targetPort: 80
```

vim tomcat-deploy-svc.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tomcat-deploy
  labels:
    app: tomcat-deploy
spec:
  replicas: 1
  selector:
    matchLabels:
      app: tomcat
  template:
    metadata:
      labels:
        app: tomcat
    spec:
      containers:
      - name: tomcat
        imagePullPolicy: IfNotPresent
        image: bitnami/tomcat
        ports:
        - containerPort: 8080
        env:
        - name: ALLOW_EMPTY_PASSWORD
          value: "yes"
---
apiVersion: v1
kind: Service
metadata:
  name: tomcat-service
spec:
  type: ClusterIP
  selector:
    app: tomcat
  ports:
    - port: 8888
      targetPort: 8080
```

查看svc到后端pod访问是否生效。

![image-20211210175745504](https://gitee.com/huowolf/pic-md/raw/master/image-20211210175745504.png)

![image-20211210175829129](https://gitee.com/huowolf/pic-md/raw/master/image-20211210175829129.png)

![image-20211210180029100](https://gitee.com/huowolf/pic-md/raw/master/image-20211210180029100.png)

## 默认后端

没有 `rules` 的 Ingress 将所有流量发送到同一个默认后端。 `defaultBackend` 通常是 [Ingress 控制器](https://kubernetes.io/zh/docs/concepts/services-networking/ingress-controllers) 的配置选项，而非在 Ingress 资源中指定。

如果 `hosts` 或 `paths` 都没有与 Ingress 对象中的 HTTP 请求匹配，则流量将路由到默认后端。

## 示例一：基于路径的匹配

该案例要实现的效果就是 ：

访问`foo.bar.com/nginx` 显示nginx的默认首页，访问`foo.bar.com/tomcat` 显示tomcat的默认首页。

vim simple-fanout-example.yaml

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: simple-fanout-example
  annotations:
        nginx.org/rewrites: "serviceName=nginx-service rewrite=/;serviceName=tomcat-service rewrite=/"
spec:
  rules:
  - host: foo.bar.com
    http:
      paths:
      - path: /nginx
        pathType: Exact
        backend:
          service:
            name: nginx-service
            port:
              number: 4200
      - path: /tomcat
        pathType: Prefix
        backend:
          service:
            name: tomcat-service
            port:
              number: 8888
  ingressClassName: nginx
```

![image-20211210180612588](https://gitee.com/huowolf/pic-md/raw/master/image-20211210180612588.png)

其中，这里进行了URL重写，如果不进行URL重写，只能路由到后端服务，但是不能显示默认首页，这是因为后端服务并没有`/nginx`、`/tomcat`这样的路由。

查看对应的ingress：

```bash
kubectl get ingress simple-fanout-example
```

## 示例二：基于域名的匹配

该案例要实现的效果就是 ：

访问`nginx.ingress.com` 显示nginx的默认首页，访问`tomcat.ingress.com` 显示tomcat的默认首页。

vim  name-virtual-host-ingress.yaml

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: name-virtual-host-ingress
spec:
  rules:
  - host: nginx.ingress.com
    http:
      paths:
      - pathType: Prefix
        path: "/"
        backend:
          service:
            name: nginx-service
            port:
              number: 4200
  - host: tomcat.ingress.com
    http:
      paths:
      - pathType: Prefix
        path: "/"
        backend:
          service:
            name: tomcat-service
            port:
              number: 8888
  ingressClassName: nginx
```

## 案例三：配置基于HTTPS的访问

该案例实现的效果是：

通过`https://https-example.foo.com/`访问nginx默认首页

1、创建自签证书文件

```bash
openssl req -x509 -nodes -newkey rsa:2048 -keyout tls.key -out tls.crt -subj "/CN=nginx/O=nginx"
```

创建后会生成两个文件 ，分别是tls.crt证书文件和tls.key私钥文件。

2、创建 secret

```bash
kubectl create secret tls tls-secret --key tls.key --cert tls.crt
```

3、创建ingress

vim tls-example-ingress.yaml

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: tls-example-ingress
spec:
  tls:
  - hosts:
      - https-example.foo.com
    secretName: tls-secret
  rules:
  - host: https-example.foo.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: nginx-service
            port:
              number: 6400
```

## 参考

1、https://kubernetes.io/zh/docs/concepts/services-networking/ingress-controllers/

2、https://kubernetes.io/zh/docs/concepts/services-networking/ingress/

3、https://docs.nginx.com/nginx-ingress-controller/installation/installation-with-manifests/

4、https://github.com/nginxinc/kubernetes-ingress/tree/v2.0.3/examples/rewrites

5、https://www.cnblogs.com/f-ck-need-u/p/7113610.html

6、https://juejin.cn/post/7003208278649864228#heading-16

