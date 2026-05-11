#!/usr/bin/env bash

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=asset-service \
--package-name=com.itservice.assetinventory \
--groupId=com.itservice.assetinventory \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
asset-service

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=customer-service \
--package-name=com.itservice.customermanagement \
--groupId=com.itservice.customermanagement \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
customer-service

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=incident-service \
--package-name=com.itservice.incidentresolution \
--groupId=com.itservice.incidentresolution \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
incident-service

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=staff-service \
--package-name=com.itservice.staffmanagement \
--groupId=com.itservice.staffmanagement \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
staff-service

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=apigateway\
--package-name=com.itservice.apigateway \
--groupId=com.itservice.apigatewa \
--dependencies=web \
--version=1.0.0-SNAPSHOT \
apigateway