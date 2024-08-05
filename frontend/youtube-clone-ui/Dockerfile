# 1. Build the Angular Application

FROM node:20-alpine AS build-stage

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

# 2. Deploy the build files on nginx

FROM nginx:alpine

COPY default.conf /etc/nginx/conf.d/default.conf

COPY --from=build-stage app/dist/youtube-clone-ui/browser /usr/share/nginx/html

EXPOSE 80
