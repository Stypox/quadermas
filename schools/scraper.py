import datetime
import hashlib
import requests
import traceback
import json

token = datetime.datetime.now().strftime("%Y%m%d") + "secret"
token = hashlib.sha256(token.encode("utf-8")).hexdigest()

municipalities = []
municipality_data = requests.get("https://www.istat.it/storage/codici-unita-amministrative/Elenco-comuni-italiani.csv").content
for line in municipality_data.split(b"\r\n"):
    line = line.split(b";")
    if len(line) < 20:
        print("WARNING, SKIPPING MUNICIPALITY LINE: ", line)
    else:
        municipalities.append((line[14].decode("utf-8"), line[19].decode("utf-8")))
print(f"Extracted {len(municipalities)} municipalities")

municipalities = municipalities
schools = {}
i = 0
for province, municipality in municipalities:
    i += 1
    print(f"{i}/{len(municipalities)} municipalities - {len(schools)} schools so far - processing {municipality} ({province})")
    try:
        url = f"https://mp.registroelettronico.com/v3/scuole/?provincia={province}&token={token}&comune={municipality}"
        resp = requests.get(url).json()
        for school in resp:
            if school["mastercom_id"] is None:
                continue
            schools[school["mastercom_id"]] = (school["nome"], school["comune"], school["provincia"])
    except KeyboardInterrupt:
        print("WARNING, SKIPPING ALL OTHER SCHOOLS")
        break
    except:
        print("ERROR, UNHANDLED EXCEPTION")
        traceback.print_exc()

output_json = []
for api_url, (name, municipality, province) in schools.items():
    output_json.append({
        "mastercom_id": api_url,
        "nome": name.title(),
        "comune": municipality.title(),
        "provincia": province.title(),
    })
json.dump(output_json, "schools.json")

def fix_string(s: str):
    return s.title().replace("\"", "\\\"")
with open("schools.java") as output_file:
    for api_url, (name, municipality, province) in schools.items():
        print(f"add(new SchoolData(\"{api_url}\", \"{fix_string(name)}\", \"{fix_string(municipality)}\", \"{fix_string(province)}\"));", file=output_file)
