Test Recrutement en microservices
---------------------------------

docker rabbit MQ
----------------
docker-compose -f rabbit.yml up -d



console RabbiqMQ
-----------------
rabbitmq-plugins.bat enable rabbitmq_management

http://localhost:15672 (guest/guest)

start server 
------------
rabbitmq-server.bat

stop server
-----------
rabbitmqctl.bat stop

rabbitmqctl.bat shutdown

reset queues
------------
    rabbitmqctl stop_app
    rabbitmqctl reset
    rabbitmqctl start_app

Links
-----
https://www.rabbitmq.com/install-windows.html

https://www.rabbitmq.com/management.html

https://stackoverflow.com/questions/33951516/cannot-enable-rabbitmq-management-plugin-on-windows

https://stackoverflow.com/questions/28258392/rabbitmq-has-nodedown-error/34538688#34538688

