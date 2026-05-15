FROM alpine:3.21
RUN apk add --no-cache python3
COPY Dockerfile ./index.html
EXPOSE 8080
CMD ["python3", "-m", "http.server", "8080"]