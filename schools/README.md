# Schools scraper

See https://github.com/Stypox/mastercom-workbook/issues/15

Fetching schools from the official APIs inside the app takes too long, as it requires one request per municipality, and there are 7900 municipalities in Italy. A while ago making a request without specifying a municipality would return all schools in italy, but it was really slow to respond nonetheless, and now it does not work anymore (504 Bad Gateway).

Now the app makes a request to [schools.json](schools.json) in this folder, which was compiled using [scraper.py](scraper.py). The script makes a request for all 7900 municipalities. It might need to be re-run from time to time to keep track of new schools being added or removed.
