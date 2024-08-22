<p align="center"> 
  <a href="https://spring.io/" target="blank"><img src="https://spring.io/img/logos/spring-initializr.svg" height="80" alt="Spring Logo" /></a> 
  <a href="https://maven.apache.org/" target="blank"><img src="https://svgrepo.com/show/354051/maven.svg" height="80" alt="Maven Logo" /></a> 
  <a href="https://www.sonarsource.com/" target="blank"><img src="https://assets-eu-01.kc-usercontent.com/64ba5402-320b-01f3-758a-878c16f16a91/12283dab-bf2b-44e3-b22f-521d2775355c/Sonar-Logo-Usage%402x.png?w=2090&h=862&auto=format&fit=crop" height="80" alt="Sonarqube Logo" /></a> 
</p> 
  
<p align="center"> 
  <a href="https://owasp.org/" target="blank"><img src="https://owasp.org/assets/images/logo.png" height="80" alt="OWASP Logo" /></a>
  <a href="https://docs.docker.com/" target="blank"><img src="https://www.docker.com/wp-content/uploads/2022/03/Moby-logo.png" height="80" alt="Docker Logo" /></a>
  <a href="https://www.jenkins.io/" target="blank"><img src="https://www.jenkins.io/images/logos/fire/256.png" height="80" alt="Jenkins Logo" /></a> 
  <a href="https://cloud.digitalocean.com/kubernetes" target="blank"><img src="https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png" height="80" alt="Kubernetes Logo" /></a> 
</p>

<p align="center">
   <a href="https://semantic-release.gitbook.io/semantic-release/" target="blank"><img src="https://raw.githubusercontent.com/semantic-release/semantic-release/master/media/semantic-release-logo.svg" height="80" alt="Semantic Release Logo" /></a> 
   <a href="https://argo-cd.readthedocs.io/en/stable/" target="blank"><img src="https://icon.icepanel.io/Technology/svg/Argo-CD.svg" height="80" alt="ArgoCD Logo" /></a> 
</p>

## Table of Contents
- [Description](#description)
- [Requirements](#requirements)
- [Setup Kubernetes (DOKS)](#setup-kubernetes-doks)
- [Setup Jenkins Master](#setup-jenkins-master-controller)
- [Setup Jenkins Agent]()
- [Setup Sonarqube](#setup-sonarqube)
- [Others](#others)

## Description

[Spring Boot](https://spring.io/) 🍃 boilerplate with [Maven](https://maven.apache.org/), fully CI-CD with [Jenkins](https://github.com/features/actions) 👺 - [ArgoCD](https://argo-cd.readthedocs.io/en/stable/) and [DOKS](https://cloud.digitalocean.com/kubernetes) 🐳

## Requirements
- Cloud: Kubernetes cluster only
- Local OS: Linux (recommend), WSL, Windows, MacOS

## Setup Kubernetes (DOKS)
- Install [doctl](https://docs.digitalocean.com/reference/doctl/how-to/install/), [kubectl](https://kubernetes.io/vi/docs/tasks/tools/install-kubectl/) on your local machine and connect to the cluster.

## Setup Jenkins Master (controller)
### You can follow by [official site](https://www.jenkins.io/doc/book/installing/kubernetes/) or just follow the steps: 
1. Create namespace named jenkins
```bash
$ kubectl create ns jenkins
```
2. Apply [service acccount](./jenkins/serviceAccount.yaml)
3. Create [volume](./jenkins/volume.yaml) note: change nodeworker's name
4. Apply [deployment](./jenkins/deployment.yaml)
5. Apply [service](./jenkins/service.yaml) if you wanna export the ip (optional)
6. Access Jenkins dashboard and install more plugins: docker, maven, kubernetes, eclipse jdk, sonarqube scanner, dependency check.
7. Add secret

## Setup Jenkins Agent
- Create a secret for building/pushing docker image:
```bash
$ kubectl create secret docker-registry dockercred \
    --docker-server=https://index.docker.io/v1/ \
    --docker-username=<dockerhub-username> \
    --docker-password=<dockerhub-password>\
    --docker-email=<dockerhub-email>
    -n jenkins
```
- In Jenkins dashboard, create a kubernetes cloud and pod template, [here](https://plugins.jenkins.io/kubernetes/) for more information.

## Setup Sonarqube
1. Deploy sonarqube to kubernetes
```bash
$ helm repo add sonarqube https://SonarSource.github.io/helm-chart-sonarqube
$ helm repo update
$ kubectl create namespace sonarqube
$ helm upgrade --install -n sonarqube sonarqube sonarqube/sonarqube
```

2. Export sonarqube
```bash
$ kubectl patch svc sonarqube-sonarqube -n sonarqube -p '{"spec": {"type": "NodePort"}}’
```
3. follow these step:
- In sonarqube dashboard, click account → security → create a pat with global scope.
- Jenkins → tools → sonarqube installations.
- Jenkins → system → sonarqube servers → enable environment variables → give a name and server url with the created token.

## Others
### ArgoCD
- Install ArgoCD in digital ocean marketplace.
- Setup with github url, point to [k8s](./k8s/).

### Nginx
- Install Nginx in digital ocean marketplace.
- export any service just by apply ingress file, [here](./jenkins/ingress.yaml) for example.

## Contact
For any questions or support, feel free to reach out via [email](mailto:nguyenducduypc160903@gmail.com)