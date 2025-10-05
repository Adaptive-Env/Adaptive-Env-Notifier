FROM ubuntu:latest
LABEL authors="wiora"

ENTRYPOINT ["top", "-b"]