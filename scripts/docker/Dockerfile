FROM nginx:alpine

# FYI
EXPOSE 80

# copy static resources into place
COPY resources/public/ /usr/share/nginx/html/

# copy main.js into place
COPY target/public/cljs-out/production/main.js /usr/share/nginx/html/main.js

# copy nginx.conf into place
COPY scripts/docker/nginx.conf /etc/nginx/nginx.conf
