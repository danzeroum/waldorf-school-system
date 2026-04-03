# --- Stage 1: build Angular ---
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json ./
# npm install (nao npm ci) pois nao ha package-lock.json no repositorio
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build -- --configuration production

# --- Stage 2: Nginx ---
FROM nginx:1.25-alpine
# Output location definido pelo angular.json: dist/waldorf-school-system
COPY --from=build /app/dist/waldorf-school-system/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
