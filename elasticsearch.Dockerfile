FROM elastic/elasticsearch:8.17.7

RUN elasticsearch-plugin install analysis-nori

USER root
RUN apk add --no cache expect curl
COPY elasticsearch-init-password.sh /usr/share/elasticsearch/init.sh
RUN chmod +x /usr/share/elasticsearch/init.sh

USER elasticsearch