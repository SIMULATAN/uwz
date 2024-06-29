#!/bin/sh

echo "Symlinking config..."
ln -s /data/options.json config.json

echo "Config: $(cat config.json)"

period="$(bashio::config 'period')"
period="${period:-60}"

echo "Starting uwz loop with period=$period..."
while true; do
  ./uwz "$@"
  sleep "$period"
done
