#!/bin/bash

clojure -A:build-prod
docker build -f scripts/docker/Dockerfile -t docker.harrigan.online/clojure/startrek-ui .
