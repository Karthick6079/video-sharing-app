# 1. Build the Angular Application

FROM node:20-alpine AS build-stage

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build --omit=dev

#RUN npm run build

#scp -r dist/* ubuntu@ip-172-31-1-255:/var/www/html/


# 2. Deploy the build files on nginx

 FROM nginx:alpine

COPY default.conf /etc/nginx/conf.d/default.conf

COPY --from=build-stage app/dist/youtube-clone-ui/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
