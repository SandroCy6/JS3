 FROM node:18-alpine as build
 WORKDIR /app
 COPY package*.json ./
 RUN npm install
 COPY . .
 RUN npm run build

 FROM nginx:alpine
 COPY --from=build /app/build /usr/share/nginx/html
 EXPOSE 80
 CMD ["nginx", "-g", "daemon off;"]

# FROM nginx:alpine
# WORKDIR /usr/share/nginx/html
# COPY build/ .
# EXPOSE 80
# CMD ["nginx", "-g", "daemon off;"]
