#! /bin/bash 
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR
echo "Deploying to tomcat...."
sudo cp ../out/production/WebSite/web.xml /var/lib/tomcat7/webapps/ROOT/WEB-INF/
sudo cp -r ../out/production/WebSite/com /var/lib/tomcat7/webapps/ROOT/WEB-INF/classes/
sudo cp -r ../out/production/WebSite/res /var/lib/tomcat7/webapps/ROOT/
sudo cp -r ../out/production/WebService/com/searchengine/ws /var/lib/tomcat7/webapps/ROOT/WEB-INF/classes/com/searchengine/
echo "Deployed successfully"
echo "Restarting tomcat server..."
sudo /etc/init.d/tomcat7 restart
