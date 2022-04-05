该项目用于学习
    DockerFile构建镜像
    推拉镜像

buil指令：
docker build [选项] <上下文路径/URL/->
    -t 镜像名:标签
    -f 指定构建文件

使用maven构建发布：
mvn clean package dockerfile:build dockerfile:push

=======================Dockerfile构建指令 start=======================
`
#### 指定基础镜像
FROM adoptopenjdk/openjdk8-openj9:alpine-slim

#### 指定维护者信息 name email
MAINTAINER SunMaoLin sunml727@126.com

#### 执行命令行命令(如下俩种方式)，注意：由于Docker是分层构建，也就是每一层指令都会建立一层，所以建议将命令写在一层cmd1 && cmd2
#### shell 格式 => RUN echo "About to start building image"
#### exec 格式 => RUN ["可执行文件","参数1","参数2"..] （推荐使用）
RUN ["echo","About to start building image"]

#### 从构建上下文目录中复制文件到镜像中(支持通配符)
#### 复制的文件会保留原有的属性读写权限等。如需要修改可以使用COPY --chown选项
#### COPY h?m* /mydir/

#### ADD与COPY基本一致。但是在COPY基础上加了一些功能
####   1. 源路径是URL，Docker会尝试下载连接文件，且将权限设置为600
####   2. 源路径是tar打包压缩文件，Docker会自动解压缩到目标路径
#### ADD h?m* /mydir/

#### Docker不是虚拟机，容器就是进程，所以启动容器时需要指定所运行的程序及参数。CMD就是用于指定默认的容器主进程的启动命令
#### 使用方式与RUN相似
####   1. shell 格式 => CMD <命令>
####   2. exec 格式 => CMD ["可执行文件","参数1","参数2"..]
####   3. exec格式在指定了ENTRYPOINT指令后，用CMD指定具体参数 （推荐使用,下一段解释）
#### shell格式在实际执行时会变更为 CMD echo $HOME => CMD ["sh","-c","echo $HOME"]。有什么影响呢？
#### 我们启动nginx命令为 service nginx start，如果我用shell格式执行CMD service nginx start会发现执行后，立即退出了。或者使用systemctl命令根本不执行。
#### 因为最后执行的是CMD ["sh","-c","service nginx start"]。对啊，执行完sh进程结束了，子进程service肯定也跟着结束了啊！
#### CMD ["nginx","-g","daemon off;"]

#### ENTRYPOINT的格式也是分为shell和exec俩种格式。当指定了ENTRYPOINT之后，CMD的含义就会发生改变。不再是运行命令，而是将CMD的参数传递给ENTRYPOINT
#### ENTRYPOINT "<CMD>" 有了CMD为什么还需要ENTRYPOINT？
#### 举例（如下构建 docker build -t myip）：
#### FROM ubuntu:18.04
#### RUN apt-get update \
####     && apt-get install -y curl \
####     && rm -rf /var/lib/apt/lists/*
#### CMD [ "curl", "-s", "http://myip.ipip.net" ]
#### 运行 docker run myip => 输出：当前 IP：61.148.226.66 来自：北京市 联通
#### 这时，如果我们想要给curl加上-i参数怎么办？docker run myip -i? 由于镜像后面跟的是command，会直接替换默认的CMD。那此时执行的不就是-i命令。这不是命令啊！
#### 那就全部替换 docker run myip curl -s heep://myvip.ipip.net -i不就好了吗。可这样是不是不妥！
#### 1. 上面说过指定了ENTRYPOINT会将CMD作为参数传递给ENTRYPOINT。2. 而且镜像后面跟的command会替换默认的CMD。
#### 那这样就好办了。这里先不给默认的CMD，直接启动时传递command给ENTRYPOINT就好
#### FROM ubuntu:18.04
#### RUN apt-get update \
####     && apt-get install -y curl \
####     && rm -rf /var/lib/apt/lists/*
#### ENTRYPOINT [ "curl", "-s", "http://myip.ipip.net" ]
#### 运行 docker run myip -i 完美！

#### ENV设置环境变量
####   1. ENV <key> <value>
####   2. ENV <key1>=<value1> <key2>=<value2>...
ENV name sml
RUN echo $name

#### ARG构建参数
#### ARG <参数名>[=<默认值>]
#### 与ENV效果相同，都是设置环境变量。所不同的是，ARG所设置的环境变量在容器运行时是不会存在的（但是不要保存密码，docker history还是会看到的）
#### ARG是定义参数名称及定义默认值，在构建的时候可使用docker build --build-arg <参数名>=<值>进行覆盖
#### 生效范围：
####   1.如果在FROM之前指定，只能用于FROM指令中（ARG name=sml \ FROM ${name}）
####   2.反之，在FROM之后指定，可以在下面使用。多个FROM必须重复指定

#### VOLUME定义匿名卷
####   1. VOLUME ["<路径1>","<路径2>"...]
####   2. VOLUME <路径>
#### 容器运行时，尽量保持容器存储层不发生写操作。因为容器删除了，数据就没了。
#### 我们可以事先指定某些目录挂载为匿名卷。这样运行时如果用户不指定挂载也可以正常运行。不会向容器存储层大量写入数据
VOLUME /data
#### 在运行时可以指定挂载进行匿名覆盖 docker run -v mydata:/data xxx

#### EXPOSE声明端口
#### EXPOSE <端口1>[<端口2>...]
#### EXPOSE声明容器运行时提供服务的端口。只是一个声明，在容器运行时并不会因为这个声明应用就会开启这个端口服务
#### 使用docker run -P时会随机端口映射到EXPOSE声明的端口
#### docker ps 会看到（暴露8080，会生成一个随机端口进行映射）
####        PORTS                    NAMES
#### 0.0.0.0:49155->8080/tcp          xxx
EXPOSE 8080

#### WORKDIR指定工作目录
#### WORKDIR <工作目录路径>
#### 指定工作目录或者说是当前目录。如果该目录不存在，WORKDIR会帮助你创建
WORKDIR /a
RUN pwd
#### => /a
WORKDIR /b
RUN pwd
#### => /b
WORKDIR /c
RUN pwd
#### => /b/c

#### USER指定当前用户
#### USER <用户名>[:<用户组>]
#### 后面的命令都会使用该用户身份进行执行

#### HEALTHCHECK健康检查
####   1.HEALTHCHECK [选项] CMD <命令> 设置检查容器健康状况的命令
####       --interval=<间隔> 俩次健康检查的间隔，默认为30s
####       --timeout=<时长> 健康检查命令运行超时时间，如果超过这个时间，本次健康检查就被视为失败。默认为30s
####       --retries=<次数> 当连续失败指定次数后，则将容器状态是为unhealthy，默认3次
####   2.HEALTHCHECK NONE 如果基础镜像有健康检查指令，使用这行可以屏蔽掉其健康检查指令
####   命令的返回值决定了该次健康检查的成功与否 0 成功 1 失败 与shell返回值相似
#### 举例：使用curl判断容器服务是否可用
HEALTHCHECK CMD curl -fs http://localhost/ || exit 1

#### LABEL用来给镜像以键值对的形式添加一些元数据
#### LABEL <key>=<value> <key>=<value>...
#### 还可以用一些标签来申明镜像的作者、文档地址等
#### 具体可以参考  https://github.com/opencontainers/image-spec/blob/master/annotations.md
LABEL org.opencontainers.image.authors="sunmaolin"

#### SHELL指令
####   SHELL ["executable","parameters"]
#### SHELL可以指定RUN ENTRYPOINT CMD指令的shell，Linux中默认为["/bin/bash","-c"]
SHELL ["/bin/sh","-cex"]

#### ONBUILD <其他指令>
#### 在以该镜像为基础镜像构建时，才会调用
#### 如：Node需要使用npm install下载包管理。那这样以该镜像为基础时可以自动执行，就不用在去写
ONBUILD RUN ["npm","install"]
`
=======================Dockerfile构建指令 end=======================


=======================Dockerfile多阶段构建 start=======================
什么是多阶段构建？
比如我想构建springboot项目。是不是只需要一个jar包就好了。那打包项目需要maven怎么办？
`FROM java
RUN 下载maven 打包
CMD 运行`
由于docker是分级构建，这样是不是多了maven这一步，导致镜像变大。我们在运行时根本不需要啊！
这时就可以使用多阶段构建，多个FROM，但是基础镜像只以最后一个为准。
`FROM maven as 别名
RUN 打包
FROM java
COPY --from=别名 xx.jar /app #从maven那一阶段将打包的项目复制到java运行环境中
CMD 运行`
=======================Dockerfile多阶段构建 end=======================










