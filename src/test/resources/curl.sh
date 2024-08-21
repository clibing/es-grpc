#!/bin/bash

curl -iv --location '127.0.0.1:51051/api/v1/login' \
--header 'Content-Type: application/json' \
--data '{
    "user": "admin",
    "password": "123456"
}'


