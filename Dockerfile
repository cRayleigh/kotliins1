FROM ubuntu:latest
LABEL authors="laczk"

ENTRYPOINT ["top", "-b"]