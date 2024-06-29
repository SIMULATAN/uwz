ARG BUILD_FROM
FROM $BUILD_FROM

WORKDIR /app

COPY entrypoint.sh .

RUN apt update && apt install -y libcurl4-gnutls-dev && rm -rf /var/lib/apt/lists/*

ARG SOURCE_IMAGE_TAG

COPY --from=ghcr.io/simulatan/uwz:$SOURCE_IMAGE_TAG /app/uwz uwz
RUN chmod +x uwz

ENTRYPOINT [ "/app/entrypoint.sh" ]
