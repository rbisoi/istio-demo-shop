#!/bin/sh
docker build --tag=demo-shop-apache apache
docker build --tag=demo-shop-postgres postgres
docker build --tag=demo-shop-shipping:1 demo-shop-shipping
docker build --tag=demo-shop-invoicing:1 demo-shop-invoicing
docker build --tag=demo-shop-order:1 demo-shop-order
