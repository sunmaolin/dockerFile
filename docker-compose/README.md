=======================docker-compose start=======================
干嘛的？
简单来讲，统一管理多个容器，进行多个容器的启停等操作。

安装
`sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose`
`sudo chmod +x /usr/local/bin/docker-compose`
`docker-compose --version`
卸载
`sudo rm /usr/local/bin/docker-compose`

注意：
docker-compose 启动时会默认以文件夹命名为前缀，创建网络和容器

# Compose 常用命令： 
docker-compose -h #查看帮助
docker-compose up #启动所有docker--compose服务
docker-compose up -d #启动所有docker-compose服务并后台运行
docker-compose down #停止并删除容器、网络、卷、镜像。
docker-compose exec yml里面的服务id #进入容器实例内部docker-compose exec docker-compose.yml文件中写的服务id /bin/bash
docker-compose ps #展示当前docker-compose编排过的运行的所有容器
docker-compose top #展示当前docker-compose编排过的容器进程
docker-compose logs yml里面的服务id#查看容器输出日志
docker-compose config #检查配置
docker-compose config -q #检查配置，有问题才有输出
docker-compose restart #重启服务
docker-compose start #启动服务
docker-compose stop #停止服务

# docker-compose文件容器配置属性：
container_name: 容器名（默认目录名_服务名_1）
image: 指定容器运行镜像（也可以通过build直接构建镜像）
build: 指定容器镜像构建文件
env_file: 指定环境变量文件（不指定默认加载当前目录下的.env文件），也可以使用docker-compose --env-file .env up指定
environment: 指定环境变量
`
enviroment:
    - name=sml
注意设置环境变量优先级 shell > .env > enviroment
`
profiles: 根据环境判断是否启用。未设置始终启用
`
俩种写法
profiles:["dev"] 开发环境才启用
profiles:
    - dev
启动时指定
docker-compose --profile dev up
`
depends_on: 依赖，用于指定启动顺序。如web依赖db就可以使用 depends_on: db
ports: 映射端口
`
ports:
    - 8080:8080
`
volumes: 挂载
command: 覆盖容器启动后默认执行命令
hostname: 网络访问名称（一个容器指定为aa，那其余容器可以通过aa访问该网络）
networks: 指定网络


# docker-compose文件网络配置属性：
`
networks:
    myNet1:
        name: myNet1 指定创建的网络名
        driver: 连接方式 bridge桥接模式 host主机模式
    default: 配置默认网络
        myNet2:
`

=======================docker-compose end=======================