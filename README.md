**This app is using Spring boot and Angular 4**
===============================================
#Start the application
> Configurations can be found in application.properties
> Start the MySQL server(On Mac, mysql.server start).  
```
gradle clean buildAngular bootRun
```
*this command clean the compiled java classes, build the angular code, copy them under the static folder and build the start the spring boot project*

#Angular build process
```
cd src/main/frontend/tvshowapp
npm run build
```
*It will build the angular app and copy the dist directory under the main/resources*

#Start ES stack
> download elasticsearch and kibana
```
cd elasticsearch-5.4.3
./bin/elasticsearch -d (start elasticsearch as daemon)
```
test elasticsearch
> curl http://localhost:9200
```
cd kibana-5.4.3-darwin-x86_64
./bin/kibana
```
test  kibana
> http://localhost:5601
install x-pack
```
cd elasticsearch-5.4.3
./bin/elasticsearch-plugin install x-pack
cd kibana-5.4.3-darwin-x86_64
./bin/kibana-plugin install x-pack
```
> after the x-pack is installed.  use elastic:changeme to visit localhost:5601
#Create index
curl --user elastic:changeme -XPUT 'localhost:9200/searchresult?pretty' 
#Delete index
curl --user elastic:changeme -XDELETE 'localhost:9200/searchresult'

