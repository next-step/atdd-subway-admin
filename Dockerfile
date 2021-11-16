From node:14-alpine3.12
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY ./frontend /usr/src/app

RUN apk --no-cache --virtual build-dependencies add \
    python2 \
    make \
    g++ \
    && npm install \
    && apk del build-dependencies
EXPOSE 8081
CMD ["npm", "run", "dev"]