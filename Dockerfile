FROM redbubble/debian-scala-runtime:master

WORKDIR /app

EXPOSE 80
ENV PORT=80

CMD "/app/bin/rb-graphql-template"

COPY app/target/universal/stage/ /app
