#!/usr/bin/env bash
set -e

echo "Waiting for Elasticsearch Running..."

until curl -u "$ELASTIC_USERNAME:$ELASTIC_PASSWORD" -s http://localhost:9200/_cluster/health?wait_for_status=yellow >/dev/null; do
  sleep 1
done

echo "Setting password for '$ELASTIC_USERNAME'..."
echo "$ELASTIC_PASSWORD" | /usr/share/elasticsearch/bin/elasticsearch-reset-password -u "$ELASTIC_USERNAME" -b -i -f

echo "Password reset completed..."

