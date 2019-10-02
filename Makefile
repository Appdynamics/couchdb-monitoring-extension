dockerRun: ##Spin up docker containers for MA with extension, controller and other apps
	@echo starting containers
	docker-compose --file docker-compose.yml up -d --build couchdb.one
	docker-compose --file docker-compose.yml up -d --build couchdb.two
	docker-compose --file docker-compose.yml up -d --build couchdb.three
	@echo done starting couchdb
	sleep 60
	curl -X POST -H "Content-Type: application/json" http://admin:admin@127.0.0.1:5984/_cluster_setup -d '{"action": "enable_cluster", "bind_address":"0.0.0.0", "username": "admin", "password":"admin", "port": 15984, "node_count": "3", "remote_node": "couchdb.two", "remote_current_user": "admin", "remote_current_password": "admin" }'
	curl -X POST -H "Content-Type: application/json" http://admin:admin@127.0.0.1:5984/_cluster_setup -d '{"action": "add_node", "host":"couchdb.two", "port": 5984, "username": "admin", "password":"admin"}'
	curl -X POST -H "Content-Type: application/json" http://admin:admin@127.0.0.1:5984/_cluster_setup -d '{"action": "enable_cluster", "bind_address":"0.0.0.0", "username": "admin", "password":"admin", "port": 25984, "node_count": "3", "remote_node": "couchdb.three", "remote_current_user": "admin", "remote_current_password": "admin" }'
	curl -X POST -H "Content-Type: application/json" http://admin:admin@127.0.0.1:5984/_cluster_setup -d '{"action": "add_node", "host":"couchdb.three", "port": 5984, "username": "admin", "password":"admin"}'
	@echo "------- Starting controller -------"
	docker-compose up -d --force-recreate controller
	#wait until controller and ES installation completes
	sleep 600
	@echo "------- Controller started -------"
	#start machine agent
	@echo ------- Starting machine agent -------
	docker-compose up --force-recreate -d --build machine
	@echo ------- Machine agent started -------

dockerStop: ##Stop and remove all containers
	@echo ------- Stop and remove containers, images, networks and volumes -------
	docker-compose down --rmi all -v --remove-orphans
	docker rmi dtr.corp.appdynamics.com/appdynamics/machine-agent:latest
	docker rmi alpine
	@echo ------- Done -------

sleep: ##sleep for x seconds
	@echo Waiting for 5 minutes to read the metrics
	sleep 300
	@echo Wait finished

dockerClean: ##Clean any left over containers, images, networks and volumes
	@if [[ -n "`docker ps -q`" ]]; then \
	docker stop `docker ps -q`; \
	fi
	docker rm -f `docker ps -a -q` || echo 0
	docker system prune -f -a --volumes