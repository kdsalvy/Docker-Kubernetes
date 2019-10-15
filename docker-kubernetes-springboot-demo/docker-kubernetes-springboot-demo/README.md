# Introduction to containerization using Docker along with Kubernetes 

## Spring Boot Application
A Simple Spring Boot application with only one API /rest/controller.

* The objective here is to containerize the application that can be executed anywhere. I have used docker as my container solution. The image used is Ubuntu to run the application.
* Filename dockerfile in root folder is docker YAML file
* Kubernetes or k8 is used here as the orchestration tool
* Multiple YAML files can be observed in the project named according to the object type

## Docker
### Docker commands to run and manage a container

* docker run {image name} : Runs an image after downloading it from docker repo if it doesn’t exist on system in attached mode.
* docker run –d {image name} : Runs an image after downloading it from docker repo if it doesn’t exist on system in detached mode.
* docker run {image name:tag} : Runs container using image with given image name of specified tag
* docker run -p {internal port}:{external port} {image name} : Runs docker and map it to external port.
* docker run -v {internal path}:{external path} {image name} : Runs docker container and map usable internal volume with the external volume
* docker run --name {container name} {image name} : Runs an image with given container name.
* docker run -e {env var key}={env var val}… {image name}: Runs an image and set given environment variables
* docker ps : Lists all the docker containers currently running.
* docker ps –a : Lists all the docker containers available.
* docker rm {container id} : Deletes a container when you don’t want to use it.
* docker images : Lists the docker images on the host.
* docker rmi {image name} : Removes the image. We should assure that no container is running for that image
* docker pull {image name} : Only downloads the image from docker repo.
* docker attach {container id} : Attaches the console again to the container with given id.
* docker inspect {container id} : Gives all the details related to a docker container.
* docker run -v {internal path}:{external path} {image name} : Runs docker container and map usable internal volume with the external volume
* docker -H={remoteIP:port} {docker commands}: Runs docker cli remotely from another system.

### Steps to create a docker image
* Create a docker file specifying environment, location of source code, entrypoint etc.
* Build the image using the docker command and docker file
	docker build {dockerfile} -t skedia/my-first-app
* Push the docker image to docker hub
	docker push skedia/my-first-app

### Sample Docker File

FROM ubuntu
RUN apt-get update
RUN apt-get install -y openjdk-8-jre
ADD ./target/spring-rest-app-1.0.jar /www/spring-rest-app-1.0.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "-Dspring.profiles.active=default", "/www/spring-rest-app-1.0.jar"]

### CMD vs ENTRYPOINT
CMD/ ENTRYPOINT is the default command to run on start of the container unless specified. In CMD if you specify anything the entire default CMD is replaced but in case of ENTRYPOINT the arguments passed are appended to the existing command.

###Docker Compose
It is configuration file in which we can put together different services and configuration specific to them. This helps in bringing up the complete application stack but on a single host only.

### Sample Docker Compose File
#### Version 1: In this version the order of service execution was not defined

	web:
	image: "skedia/my-first-app"
	ports:
	- "8080:8080"
	links:
	- database
	
	database:
	image: "mysql"
	ports:
	- "3307:3306"
	environment:
	- "MYSQL_ROOT_PASSWORD=admin"
	- "MYSQL_USER=root"


#### Version 2: Service Execution order and new format is defined in this

	version: "2"
	services:
	    web:
	        image: "skedia/my-first-app"
	        ports: 
	        - "8080:8080"
	        links:
	        - database
	    
	    database:
	        image: "mysql"
	        ports: 
	        - "3307:3306"
	        environment:
	        - "MYSQL_ROOT_PASSWORD=admin"
	        - "MYSQL_USER=root"

#### Version 3: Integrated Docker swarm to this

	version: "3"
	services:
	    web:
	        image: "skedia/my-first-app"
	        ports: 
	        - "8080:8080"
	        links:
	        - database
	    
	    database:
	        image: "mysql"
	        ports: 
	        - "3307:3306"
	        environment:
	        - "MYSQL_ROOT_PASSWORD=admin"
	        - "MYSQL_USER=root"

### Docker Engine, Storage & Networking
On installing Docker we get three components which talk to each other using rest api.
* Docker Daemon
* Docker Rest API
* Docker CLI 

### Namespace PID
The process in a container have their own PIDs which made it to believe they are running standalone but actually they are mapped to the PIDs in the host.

### File System
* Volume Mounting (can map only /var/lib/volumes on host)
* Bind Mounting (can map any path on host)

### Volume Mounting
* Create a volume using docker volume create {volume name}
* Mount the volume to the location which you want to save using 
	```docker run -v {volume name}:{path} {image name}```
Docker will automatically create a volume if the given name doesn’t exist

### Bind Mounting
	docker run –v {complete folder path on host}:{path on container} {image name}

New format
	docker run \
	--mount type=bind,source={source full path},target={target full path} {image name}

### Networking
* Lists all the network settings
	```docker inspect {container name}```
Containers can reach each other using the container name
	```docker network ls
	docker run –network={bridge(by default)/host/none} {image name}
	docker network create \
	--driver bridge \
	--subnet {subnet ip/port}
	{custom isolated network name}```
* Example:
	```docker network create --driver bridge --subnet 182.18.0.1/24 wp-mysql-network --gateway 182.18.0.1```

## Kubernetes

Kubernetes is a platform to manage containerized workloads and services

Benefits of using Kubernetes:
* Service discovery and load balancing
* Storage orchestration
* Automated rollouts and rollbacks
* Automatic bin packing
* Self-healing
* Secret and configuration management

### Pod 
Smallest component of the system. Generally have 1:1 relation with containers except in case of helper container where it becomes the part of the same pod with main container.

### Replication Controller
Old technology to create replicas in order to scale our application

### Replica Set
New version of replication controller

### Deployment
Deployment encapsulates replica-set and provides way to ensure rolling updates and rollback.

###Updates and rollout in deployment
we can update the image version, labels, selectors etc. We can set the strategy as rollout (default) which deploys-destroys one container at a time.

## Kubernetes Service

Enables communication between applications or users outside of the environment. Enables loose coupling between micro-services in our application.
* ClusterIP: Creates a cluster ip that can be used internally by the services
* NodePort: Listens & maps a port on the node to the port on the pod and make it accessible to the outside world.
* LoadBalancer: Creates and exposes external IP on the node and is used to cofigure ingress rules.

### Kubernetes Components

* Master components: They provide the cluster’s control plane. Master components make global decisions about the cluster 
	* kube-apiserver: Component on the master that exposes the Kubernetes API
	* etcd: Consistent and highly-available key value store used as Kubernetes’ backing store for all cluster data.
	* kube-scheduler: Component on the master that watches newly created pods that have no node assigned, and selects a node for them to run on.
	* kube-controller-manager: 

### Kubernetes Commands:
* To create container: creates and runs a pod/deployment/service defined in k8.yml
	kubectl create -f k8.yml
	
* To expose container:
	kubectl expose pod myapp-pod --type=NodePort --name=myapp-service
	
* To check container’s port exposed:
	kubectl describe service myapp-service
	
* Other generally used commands:
	* kubectl run {name} --image {image name on docker hub}
	* kubectl get pods
	* kubectl describe pod {pod name}
	* kubectl get pods -o wide
	* kubectl get replicationcontroller
	* kubectl get replicaset
	* kubectl replace -f {replica set yaml file}
	* kubectl create -f {replica set yaml file}
	* kubectl delete replicaset {replica-set name}
	* kubectl scale --replicas=6 -f {replica set yaml file}
	* kubectl scale --replicas=6 replicaset {replica-set name}
	* kubectl create -f {deployment yaml file}
	* kubectl rollout status {deployment name}
	* kubectl rollout history {deployment name}
	* kubectl apply -f {deployment yaml file}
	* kubectl rollout undo {deployment yaml file}

### Networking in Kubernetes
We need a networking add-on for the nodes to communicate with each other. Using IP may not be a good idea as it may get changed on each deployment.


### Kubernetes YAML structure

	apiVersion:
	kind:
	metadata:
	spec:

### Kubernetes POD sample YAML 

	apiVersion: v1
	kind: Pod
	metadata:
	  name: myapp-pod
	  labels:
	    app: myapp
	    type: backend
	spec:
	  containers:
	    - name: springboot-docker
	      image: skedia/docker-kubernetes-springboot-demo

* apiVersion is the kubernetes version that we want kubectl to use. Below table gives different versions for different kinds
	
		Kind        |  Version  |
		----------- | --------- |
		POD         |  v1       |
		Service     |  v1       |
		ReplicaSet  |  apps/v1  |
		Deployment  |  apps/v1  |
                            
* Kind refers to the type of object we intend to use
* Metadata is the details about the object being deployed
	metadata:
	    name: myapp-pod
	    labels:
	        app: myapp
	        type: front-end
* Spec contains the specifications required for the kind we are using. Different kinds need different specification which can be looked upon on kubernetes site. Spec is dictionary type and containers is 
	spec:
	    containers:
	        - name: {container name}
	          image: {image name}

### Replication Controller sample YAML

	apiVersion: v1
	kind: ReplicationController
	metadata:
	  name: myapp-replication-controller
	  labels:
	    app: myapp
	    type: backend
	spec:
	  template:
	    metadata:
	      name: myapp-rc
	      labels:
	        app: myapp
	        type: backend
	    spec:
	      containers:
	        - name: springboot-docker
	          image: skedia/docker-kubernetes-springboot-demo
	  replicas: 3

Template section under spec gives the pod definition to be replicated in order to scale the application.

### Replica Set sample YAML

	apiVersion: apps/v1
	kind: ReplicaSet
	metadata:
	  name: myapp-replica-set
	  labels:
	    app: myapp
	    type: backend
	spec:
	  template:
	    metadata:
	      name: myapp-pod
	      labels:
	        app: myapp
	        type: backend
	    spec:
	      containers:
	        - name: springboot-docker
	          image: skedia/docker-kubernetes-springboot-demo
	          ports: 
	            - name: http
	              containerPort: 8080
	              protocol: TCP
	  replicas: 3
	  selector:
	    matchLabels:
	      type: backend

Selector is the major difference between replication controller and replica set

#### Why labels and selectors are required?

Replica set can be used to monitor existing pods, if they are not created then replica set will create them. Replica set uses labels to select specific pods to monitor them.

### Deployment sample YAML

	apiVersion: extensions/v1beta1
	kind: Deployment
	metadata: 
	  name: myapp-deployment
	  labels:
	    app: myapp
	    type: backend
	spec: 
	  replicas: 3
	  selector:
	    matchLabels: 
	      type: backend
	  template: 
	    metadata: 
	      labels: 
	        app: myapp
	        type: backend
	      name: myapp-pod
	    spec: 
	      containers: 
	        - 
	          image: skedia/docker-kubernetes-springboot-demo
	          name: springboot-docker

Deployment create replica-sets and pods under them.

### ClusterIP Service sample YAML

	apiVersion: v1
	kind: Service
	metadata:
	  name: myapp-clusterip-service
	  labels:
	    app: myapp
	spec:
	  type: ClusterIP
	  selector:
	    app: myapp
	    type: backend
	  ports:
	    - port: 80 # service port
	      targetPort: 8080
	  clusterIP: 10.96.0.2

### NodePort Service sample YAML

	apiVersion: v1
	kind: Service
	metadata:
	  name: myapp-nodeport-service
	  labels:
	    app: myapp
	spec:
	  type: NodePort
	  selector:
	    app: myapp
	    type: backend
	  ports:
	    - port: 80 # service port
	      targetPort: 8080 # application port
	      nodePort: 30080 # node port 
	  clusterIP: 10.96.0.2

## LoadBalancer Service sample YAML

	apiVersion: v1
	kind: Service
	metadata:
	  name: myapp-loadbalancer-service
	  labels:
	    app: myapp
	spec:
	  type: LoadBalancer
	  selector:
	    app: myapp
	    type: backend
	  ports:
	    - port: 80 # service port
	      targetPort: 8080 # application port
	      nodePort: 30080 # node port
	  clusterIP: 10.96.0.2
	status:
	  loadBalancer:
	    ingress:
	      - ip: 192.0.2.127
