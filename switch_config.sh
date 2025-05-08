#!/bin/bash

if [[ "$#" -eq 1 ]]; then
	case $1 in
		-otn | --oldToNew)
			echo "Copying configuration to httpd.conf..."
                	sudo cp -f /etc/httpd/conf/httpd_new.conf /etc/httpd/conf/httpd.conf
                	echo "Restarting httpd service..."
                	sudo systemctl restart httpd
                	echo "Successfully restarted service"
			;;
		-nto | --newToOld)
			echo "Copying configuration to httpd.conf..."
                	sudo cp -f /etc/httpd/conf/httpd_old.conf /etc/httpd/conf/httpd.conf
                	echo "Restarting httpd service..."
                	sudo systemctl restart httpd
                	echo "Successfully restarted service"
			sudo pm2 stop all
			;;
	esac	
fi
