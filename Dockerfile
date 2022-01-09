FROM ubuntu:latest

RUN apt-get update && apt-get -y install cron

COPY ./build/install/trefu-scraper/ /etc/cron.d/trefu-scraper

RUN chmod 0644 /etc/cron.d/trefu-scraper/bin/trefu-scraper

RUN crontab -l | { cat; echo "* * * * * bash /etc/cron.d/trefu-scraper/bin/trefu-scraper"; } | crontab -

CMD cron

CMD /etc/cron.d/trefu-scraper/bin/trefu-scraper