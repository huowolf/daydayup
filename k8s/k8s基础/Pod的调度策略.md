## 1、默认调度

Kubernetes 集群的默认调度是由kube-scheduler经过一系列算法计算得出。

kube-scheduler 给一个 pod 做调度选择包含两个步骤：

​	1、过滤：将所有满足pod调度需求的node选出来；

​	2、打分：为pod从所有可调度节点中选取一个最合适的node;

## 2、定向调度 

### 2.1 NodeName

**NodeName强制约束将Pod调度到指定Name的Node节点上。**其跳过kube-scheduler的调度逻辑，直接将pod调度到指定名称的节点上。

**实验**

```bash
#1、创建node-name.yaml文件，配置nodeName字段
apiVersion: v1
kind: Pod
metadata:
  name: nginx-nodename
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:latest
    imagePullPolicy: IfNotPresent
  nodeName: node3
#2、应用配置文件，然后查看分配给pod的“NODE”来验证其是否有效
#由于kubernetes集群没有node3的节点，所以pod状态为Pending，调度不成功。
kubectl get pod -n dev -o wide
```

![image-20211130111311038](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130111311038.png)

### 2.1 NodeSelector

**将pod调度到添加了指定标签的node节点上。**其通过kubernetes的label-selector机制实现，在pod创建前，会由scheduler使用MatchNodeSelector调度策略进行label匹配，找到目标node，将pod调度到目标节点上。

```bash
#1、获取集群的节点名称
[root@master ~]# kubectl get nodes
NAME     STATUS   ROLES    AGE     VERSION
master   Ready    <none>   2d10h   v1.20.10
node1    Ready    <none>   2d9h    v1.20.10
node2    Ready    <none>   2d9h    v1.20.10

#2、选择要添加标签的节点，将标签添加到节点上
#命令格式：kubectl label nodes <node-name> <label-key>=<label-value>
kubectl label nodes node1 env=test

#3、查看节点的标签信息
kubectl get nodes --show-labels
#或使用kubectl describe node "nodename"查看节点完整信息
```

![image-20211130102350120](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130102350120.png)

**测试**

```bash
#1、创建node-selector.yaml文件，将nodeSelector字段添加到pod配置中
apiVersion: v1
kind: Pod
metadata:
  name: nginx-nodeselector
  namespace: dev
spec:
  containers:
  - name: nginx
    image: nginx:latest
    imagePullPolicy: IfNotPresent
  nodeSelector:
    env: test
#2、应用配置文件，pod将会调度到添加标签的节点上
kubectl apply -f node-selector.yaml
#3、查看分配给pod的“NODE”来验证其是否有效
kubectl get pod -n dev -o wide
```

![image-20211130110321414](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130110321414.png)

## 3、亲和性调度

亲和性调度会优先选择满足条件的Node进行调度，若没有，也可以调度到不满足条件的节点上，使调度更灵活。

### 3.1 NodeAffinity(Node亲和性)

node亲和性以node为目标 ，解决pod可以调度到哪些node上的问题

节点亲和性有两种类型：

1、 `requiredDuringSchedulingIgnoredDuringExecution`**硬限制**

2、 `preferredDuringSchedulingIgnoredDuringExecution`**软限制**

**节点亲和性通过 PodSpec 的 `affinity` 字段下的 `nodeAffinity` 字段进行指定。**

**测试**

```bash
#1、使用node-affinity.yaml创建节点亲和性的Pod 
apiVersion: v1
kind: Pod
metadata:
  name: nginx-node-affinity
  namespace: dev
spec:
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: env
            operator: In
            values:
            - test
            - example
      preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 1
        preference:
          matchExpressions:
          - key: envnode
            operator: In
            values:
            - example2
  containers:
  - name: nginx-node-affinity
    image: nginx:latest
```

**注解**

```bash
#(1)此节点亲和性规则表示，pod只能调度到具有标签键env且标签值为test或example的节点上。另外，在满足这些规则的节点中，具有标签键为envnode2且标签值为example2的节点应该优先使用。
#(2)操作符：In，NotIn，Exists，DoesNotExist，Gt，Lt。
#(3)若pod同时指定nodeSelector和nodeAffinity，两者必须都要满足，才能将Pod调度到候选节点上。
#(4)若pod指定多个nodeSelectorTerms，只需其中一个即可。
#(5)若一个nodeSelectorTerms中有多个matchExpressions，则要满足所有的matchExpressions。
#(6)若pod所调度到的Node在pod运行期间标签发生了改变，Pod不会被删除。亲和性选择只在Pod调度期间有效。
#(7)preferredDuringSchedulingIgnoredDuringExecution中weight字段值的范围是1-100。
```

查看节点的标签信息

![image-20211130141601098](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130141601098.png)

查看pod调度结果，新pod调度到 node2上。

![image-20211130153455426](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130153455426.png)

### 3.2 PodAffinity(Pod亲和性)

**以pod为目标，解决pod可以和哪些已存在的pod部署在同一节点的问题**

Pod亲和性有两种类型：

1、 `requiredDuringSchedulingIgnoredDuringExecution`**硬限制**

2、 `preferredDuringSchedulingIgnoredDuringExecution`**软限制**

**测试**

```bash
#1、创建node1上的参照pod，pod-target.yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-affinity-target
  namespace: dev
  labels:
    podenv: pro
spec:
  containers:
  - name: nginx
    image: nginx:latest
  nodeName: node1
#2、创建node2上的参照pod，pod-target-node2.yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-affinity-target-node2
  namespace: dev
  labels:
    podenv: pro
spec:
  containers:
  - name: nginx
    image: nginx:latest
  nodeName: node2
#3、创建亲和性测试pod，pod-affinity.yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-affinity-required
  namespace: dev
spec:
  affinity:
    podAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
          - key: podenv
            operator: In
            values:
            - pro
            - pro2
        topologyKey: kubernetes.io/hostname
      preferredDuringSchedulingIgnoredDuringExecution: 
      - weight: 100  
        podAffinityTerm:
          labelSelector:
            matchExpressions:
            - key: podenv 
              operator: In 
              values:
              - pro
          topologyKey: kubernetes.io/hostname
  containers:
  - name: nginx
    image: nginx:latest
#4、注解
#pod亲和性规则表示，新创建的pod可以运行在标签为podenv=pro或podenv=pro2的pod所对应的node节点上。并且该node具有kubernetes.io/hostname标签。另外，在满足这些规则的节点中，具有标签podenv=pro2pod所在node且node具有kubernetes.io/hostname标签优先使用。
```

查看pod的标签信息

![image-20211130162857587](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130162857587.png)

查看pod亲和性调度结果，新pod调度到node2上。

![image-20211130162420198](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130162420198.png)

### 3.3 PodAntiAffinity(Pod反亲和性)

**以pod为目标，解决pod不可以和哪些已存在的pod部署在同一节点上的问题**

pod反亲和性有两种类型：

1、 `requiredDuringSchedulingIgnoredDuringExecution`**硬限制**

2、 `preferredDuringSchedulingIgnoredDuringExecution`**软限制**

**测试**

```bash
#1、参照pod仍旧使用pod亲和性调度实验中在node1和node2上创建的pod

#2、创建反亲和性测试pod，pod-anti-affinity.yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-anti-affinity-required
  namespace: dev
spec:
  affinity:
    podAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
          - key: podenv
            operator: In
            values:
            - pro
            - pro2
        topologyKey: kubernetes.io/hostname
  containers:
  - name: nginx
    image: nginx:latest
#3、注解
#pod反亲和性规则表示，新创建的pod与拥有标签envnode=pro或envnode=pro2的pod不在同一node上。
```

查看pod反亲和性调度结果：新pod调度到master节点上

![image-20211130171402105](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211130171402105.png)

```bash
1、pod亲和性与反亲和性基于已经在节点上运行的Pod的标签来约束Pod可以调度到的节点，而不是基于节点的标签
2、对于Pod亲和性与反亲和性中topologyKey的详细介绍可以参考: http://bazingafeng.com/2019/03/31/k8s-affinity-topologykey/
```

**pod间亲和性与反亲和性在与更高级别的集合（如 ReplicaSets、StatefulSets、 Deployments 等）一起使用时，可能更加有用**

**测试**

```bash
#redis-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis-cache
spec:
  selector:
    matchLabels:
      app: store
  replicas: 3
  template:
    metadata:
      labels:
        app: store
    spec:
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
          - labelSelector:
              matchExpressions:
              - key: app
                operator: In
                values:
                - store
            topologyKey: "kubernetes.io/hostname"
      containers:
      - name: redis-server
        image: redis:latest
```

Deployment配置了PodAntiAffinity，用来确保调度器不会将副本调度到单个节点上。

```bash
#nginx-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: web-server
spec:
  selector:
    matchLabels:
      app: web-store
  replicas: 3
  template:
    metadata:
      labels:
        app: web-store
    spec:
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
          - labelSelector:
              matchExpressions:
              - key: app
                operator: In
                values:
                - web-store
            topologyKey: "kubernetes.io/hostname"
        podAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
          - labelSelector:
              matchExpressions:
              - key: app
                operator: In
                values:
                - store
            topologyKey: "kubernetes.io/hostname"
      containers:
      - name: web-app
        image: nginx:latest
```

Deployment配置了podAntiAffinity和podAffinity，确保调度器将副本与具有app=store标签的pod放置在一起，还确保副本不会调度到单个节点上

查看pod调度结果：

![image-20211201101759580](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211201101759580.png)

## 4、污点(容忍)调度

前面的调度方式都是在pod的角度上，通过在pod上添加属性，来确定pod是否可以调度到指定node上。同样也可以在node角度上，通过为node添加污点属性，来决定是否允许pod调度过来。

### 污点

污点格式：`key=value:effect`，key和value是污点标签的键和值，effect描述污点的作用。

effect有一下三种选项：

​	**PreferNoSchedule**：尽量避免把pod调度到具有该污点的node上，除非没有其他节点可调度；

​	**NoSchedul**：不会把pod调度到具有该污点的node上，但不会影响当前node上已存在的pod；

​	**NoExecute**：不会把pod调度到具有该污点的node上，同时也会将Node上已存在的pod驱逐；

​		**驱逐情况：**

​		1、若pod不能忍受effect值为NoExecute的污点，pod会马上被驱逐；

​		2、若pod能忍受effect值为NoExecute的污点，但在容忍度定义中没有指定tolerationSeconds，则pod还会一直在这个节点上运行；

​		3、若pod能忍受effect值为NoExecute的污点，并指定tolerationSeconds，则pod还能在节点上运行指定时间；

**相关命令**

```bash
#设置污点
kubectl taint node node1 key=value:effect
#去除污点
kubectl taint node node1 key=value:effect-
```

### 容忍

容忍度应用于pod 上，允许(但并不要求)pod调度到带有与之匹配的污点的节点上。

**测试**

```bash
#1、给集群中节点添加污点
kubectl taint nodes node1 key1=value1:NoSchedule
kubectl taint nodes node2 key2=value2:NoSchedule
kubectl taint nodes master example-key=value3:NoSchedule
#2、创建一个容忍的pod
apiVersion: v1
kind: Pod
metadata:
  name: nginx
  namespace: dev
  labels:
    env: test
spec:
  containers:
  - name: nginx
    image: nginx:latest
    imagePullPolicy: IfNotPresent
  tolerations:
  - key: "example-key"
    operator: "Exists"
    effect: "NoSchedule"
#3、查看pod的调度结果
```

![image-20211201110428931](https://gitee.com/blingblingya/markdown/raw/master/img/image-20211201110428931.png)

**说明**

```bash
#1、operator的默认值是equal，污点和容忍度相匹配是指具有一样的键名和效果，如果operator是Exists，容忍度不能指定value，如果operato是Equal，则value应相等。
#2、如果容忍度的key为空且operator为Exists，表示容忍度能容忍任意的taint；
#3、如果effect为空，则可以与所有键名example-key的效果相匹配；
```

## 5、pod优先级

优先级表示一个pod相较于其它pod的重要性，如果pod无法调度，调度程序会尝试抢占较低优先级的pod。

**优先级的使用**

1、新增PriorityClass：

​		名称在 `name` 字段中指定；

​		值在`value`字段指定，值越大，优先级越高；

​		可选字段：`globalDefaul`和`description`

​		`globalDefaul`：表示PriorityClass 的值应用于没有 `priorityClassName` 的Pod。

​		`description`：字段是一个任意字符串。

2、创建pod，并将其PriorityClassName设置为新增的PriorityClass

**非抢占式PriorityClass**

`PreemptionPolicy` 字段默认为`PreemptLowerPriority`，这允许pod抢占较低优先级的pod；若配置成never后，pod被放置在调度队列中较低优先级pod之前，但不抢占其他pod；

 **pod优先级对调度顺序的影响**

当启用pod优先级时，调度顺序会按优先级对Pending pod进行排序，优先级高的pending pod会被放置在调度队列中低优先级的Pending pod之前。因此，满足调度要求时，高优先级的pod比低优先级的pod更早调度。
