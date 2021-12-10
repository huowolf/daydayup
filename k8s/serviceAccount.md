# Service Account

## User Account 和Service Account

- 用户账号是针对人而言的。 服务账号是针对运行在 Pod 中的进程而言的。
- 用户账号是全局性的，其名称跨集群中名字空间唯一的。服务账号是名字空间作用域的。

## Service Account中的三个组件

三个独立组件协作完成服务账号相关的自动化：

- `ServiceAccount` 准入控制器
- Token 控制器
- `ServiceAccount` 控制器

### `ServiceAccount` 准入控制器

对 Pod 的改动通过一个被称为 [准入控制器](https://kubernetes.io/zh/docs/reference/access-authn-authz/admission-controllers/) 的插件来实现。它是 API 服务器的一部分。 当 Pod 被创建或更新时，它会同步地修改 Pod。 如果该插件处于激活状态（在大多数发行版中都是默认激活的），当 Pod 被创建 或更新时它会进行以下操作：

1. 如果该 Pod 没有设置 `ServiceAccount`，将其 `ServiceAccount` 设为 `default`。
2. 保证 Pod 所引用的 `ServiceAccount` 确实存在，否则拒绝该 Pod。
3. 如果服务账号的 `automountServiceAccountToken` 或 Pod 的 `automountServiceAccountToken` 都未显式设置为 `false`，则为 Pod 创建一个 `volume`，在其中包含用来访问 API 的令牌。
4. 如果前一步中为服务账号令牌创建了卷，则为 Pod 中的每个容器添加一个 `volumeSource`，挂载在其 `/var/run/secrets/kubernetes.io/serviceaccount` 目录下。
5. 如果 Pod 不包含 `imagePullSecrets` 设置，将 `ServiceAccount` 所引用 的服务账号中的 `imagePullSecrets` 信息添加到 Pod 中。

### Token 控制器

TokenController 作为 `kube-controller-manager` 的一部分运行，以异步的形式工作。 其职责包括：

- 监测 ServiceAccount 的创建并创建相应的服务账号令牌 Secret 以允许访问 API。
- 监测 ServiceAccount 的删除并删除所有相应的服务账号令牌 Secret。
- 监测服务账号令牌 Secret 的添加，保证相应的 ServiceAccount 存在，如有需要， 向 Secret 中添加令牌。
- 监测服务账号令牌 Secret 的删除，如有需要，从相应的 ServiceAccount 中移除引用。

###  `ServiceAccount` 控制器

服务账号控制器管理各名字空间下的 ServiceAccount 对象，并且保证每个活跃的 名字空间下存在一个名为 "default" 的 ServiceAccount。

## 实践

## 1、查看默认的sa及secret

查看默认的sa

![image-20211201143427720](https://gitee.com/huowolf/pic-md/raw/master/image-20211201143427720.png)

查看sa详情

![image-20211201143617033](https://gitee.com/huowolf/pic-md/raw/master/image-20211201143617033.png)

可以看到，sa关联了一个secret。

查看这个secret

![image-20211201144654414](https://gitee.com/huowolf/pic-md/raw/master/image-20211201144654414.png)

查看secret详情

![image-20211201144901575](https://gitee.com/huowolf/pic-md/raw/master/image-20211201144901575.png)

可以看到这个secret的类型为`kubernetes.io/service-account-token`，同时secret的内容包含三个文件，分别为ca.crt、namespace和token。

## 2、pod使用默认的sa访问apiServer

先创建一个deploy

nginx-deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
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
        image: nginx:alpine
        ports:
        - containerPort: 80
```

查看pod

![image-20211201145412570](https://gitee.com/huowolf/pic-md/raw/master/image-20211201145412570.png)

当你创建 Pod 时，如果没有指定服务账户，Pod 会被指定给命名空间中的 `default` 服务账户。 如果你查看 Pod 的原始 JSON 或 YAML（例如：`kubectl get pods/podname -o yaml`）， 你可以看到 `spec.serviceAccountName` 字段已经被自动设置了。

![image-20211201150155818](https://gitee.com/huowolf/pic-md/raw/master/image-20211201150155818.png)

进入pod，查看默认挂载的secret对应的数据卷

![image-20211201145846367](https://gitee.com/huowolf/pic-md/raw/master/image-20211201145846367.png)

下面开始查看这三个文件

![image-20211201151308397](https://gitee.com/huowolf/pic-md/raw/master/image-20211201151308397.png)

![image-20211201151358065](https://gitee.com/huowolf/pic-md/raw/master/image-20211201151358065.png)

![image-20211201151433103](https://gitee.com/huowolf/pic-md/raw/master/image-20211201151433103.png)

ca.crt：根证书，用于Client端验证API Server发送的证书
namespace：标识这个service-account-token的作用域空间
token：使用API Server私钥签名的JWT，用于访问API Server时，Server端的验证

## 3、了解上述三个文件到底有啥用？

使用nginx:alpine 镜像来手动访问apiserver

在容器应用中，可以使用ca.crt和Token来访问API Server。

下面来验证一下认证是否能生效。在Kubernetes集群中，默认为API Server创建了一个名为kubernetes的Service，通过这个Service可以访问API Server。

进入Pod，使用curl命令直接访问会得到如下返回信息，表示并没有权限。

![image-20211201153212679](https://gitee.com/huowolf/pic-md/raw/master/image-20211201153212679.png)

使用ca.crt和Token做认证，先将ca.crt放到CURL_CA_BUNDLE这个环境变量中，curl命令使用CURL_CA_BUNDLE指定证书；再将Token的内容放到TOKEN中，然后带上TOKEN访问API Server。

![image-20211201153336792](https://gitee.com/huowolf/pic-md/raw/master/image-20211201153336792.png)

```
# export CURL_CA_BUNDLE=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
# TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
# curl -H "Authorization: Bearer $TOKEN" https://kubernetes
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {

  },
  "status": "Failure",
  "message": "forbidden: User \"system:serviceaccount:default:sa-example\" cannot get path \"/\"",
  "reason": "Forbidden",
  "details": {

  },
  "code": 403
}
```

可以看到，已经能够通过认证了，但是API Server返回的是cannot get path \"/\""，表示没有权限访问，这说明还需要得到授权后才能访问。

参考：https://support.huaweicloud.com/basics-cce/kubernetes_0032.html

## 4、使用自定义sa

你可以像这样来创建额外的 ServiceAccount 对象：

```yaml
kubectl create -f - <<EOF
apiVersion: v1
kind: ServiceAccount
metadata:
  name: build-robot
EOF
```

如果你查询服务帐户对象的完整信息，如下所示：

```bash
kubectl get serviceaccounts/build-robot -o yaml
```

那么你就能看到系统已经自动创建了一个令牌并且被服务账户所引用。

然后可以使用RBAC机制来为sa设置访问权限。

要使用非默认的服务账户，将 Pod 的 `spec.serviceAccountName` 字段设置为你想用的服务账户名称。

Pod 被创建时服务账户必须存在，否则会被拒绝。

你不能更新已经创建好的 Pod 的服务账户。

你可以清除服务账户，如下所示：

```bash
kubectl delete serviceaccount/build-robot
```

## 5、从Dashboard组件的授权来看sa

由于 Kubernetes Dashboard 默认部署时，只配置了最低权限的 RBAC。因此，我们要创建一个名为 `admin-user` 的 ServiceAccount，再创建一个 ClusterRolebinding，将其绑定到 Kubernetes 集群中默认初始化的 `cluster-admin` 这个 ClusterRole。

#### 5.1 先创建sa

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
```

#### 5.2 创建ClusterRolebinding

kubadm安装的k8s集群默认已经存在`cluster-admin`角色，只需要将它绑定到sa上即可。

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
```

#### 5.3 获取token

```bash
kubectl -n kubernetes-dashboard get secret $(kubectl -n kubernetes-dashboard get sa/admin-user -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"
```

![image-20211201162746668](https://gitee.com/huowolf/pic-md/raw/master/image-20211201162746668.png)

#### 5.4 冷静下来分析一下获取token这个命令

1、根据sa名字获取对应的secrect名字。

```bash
kubectl -n kubernetes-dashboard get sa/admin-user -o json
```

![image-20211201163425733](https://gitee.com/huowolf/pic-md/raw/master/image-20211201163425733.png)

2、通过secret获取对应的token

```bash
kubectl -n kubernetes-dashboard get secret admin-user-token-7xp2t -o json
```

![image-20211201163646843](https://gitee.com/huowolf/pic-md/raw/master/image-20211201163646843.png)

so，就是这么easy，下次如果记不清楚上面获取token的命令，就用这两条命令来搞定吧！

#### 5.5 dashboard参考

https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md

http://press.demo.kuboard.cn/install/install-k8s-dashboard.html

## 参考

1、https://kubernetes.io/zh/docs/reference/access-authn-authz/service-accounts-admin/

2、https://kubernetes.io/zh/docs/tasks/configure-pod-container/configure-service-account/

3、https://kubernetes.io/zh/docs/reference/access-authn-authz/authentication/#service-account-tokens