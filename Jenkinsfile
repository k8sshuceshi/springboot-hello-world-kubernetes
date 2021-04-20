pipeline {
    agent any
    environment {
        // 定义 Docker 镜像名称，账号/镜像名
        DOCKER_IMAGE_NAME = "k8sshuceshi/springboot-hello-world-kubernetes"
        CANARY_REPLICAS = 1
    }
    stages {
        stage('Build') {
            steps {
                echo '运行构建自动化'
                sh './gradlew build --no-daemon'
                archiveArtifacts artifacts: 'build/libs/spring-boot-0.0.1-SNAPSHOT.jar'
            }
        }
        stage('Build Docker Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    // 构建 Docker 镜像
                    app = docker.build(DOCKER_IMAGE_NAME)
                    app.inside {
                        sh 'echo 你好，世界！'
                    }
                }
            }
        }
        stage('Push Docker Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker_hub_login') {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        stage('CanaryDeploy') {
            when {
                branch 'master'
            }
            // 金丝雀部署 1 个 Pod
            environment {
                CANARY_REPLICAS = 1
            }
            steps {
                // 根据配置的 springboot-hello-world-kube-canary.yml 模版来部署镜像至 Kubernetes
                kubernetesDeploy(
                    kubeconfigId: 'kubeconfig',
                    configs: 'springboot-hello-world-kube-canary.yml',
                    enableConfigSubstitution: true
                )
            }
        }
        stage('SmokeTest') {
            when {
                branch 'master'
            }
            steps {
                script {
                    sleep (time: 20)
                    def response = httpRequest (
                        url: "http://$KUBERNETES_MASTER_IP:8081/",
                        timeout: 30
                    )
                    echo response.status
                    if (response.status != 200) {
                        error("金丝雀冒烟测试失败！")
                    }
                }
            }
        }
        stage('DeployToProduction') {
            when {
                branch 'master'
            }
            steps {
                milestone(1)
                // 根据配置的 springboot-hello-world-kube.yml 模版来部署镜像至 Kubernetes
                kubernetesDeploy(
                    kubeconfigId: 'kubeconfig',
                    configs: 'springboot-hello-world-kube.yml',
                    enableConfigSubstitution: true
                )
            }
        }
    }
    post {
        cleanup {
            kubernetesDeploy(
                kubeconfigId: 'kubeconfig',
                configs: 'springboot-hello-world-kube-canary.yml',
                enableConfigSubstitution: true
            )
        }
    }
}
