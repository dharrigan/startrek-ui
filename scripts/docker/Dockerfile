FROM nginx:alpine

# FYI
EXPOSE 80

# copy static resources into place
COPY public/ /usr/share/nginx/html/

# copy nginx.conf into place
COPY scripts/docker/nginx.conf /etc/nginx/nginx.conf
