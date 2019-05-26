import urllib.request
import zlib
import json
from datetime import datetime
import enum

class MastercomWorkbook:
	authenticateUrl = "https://rosmini-tn.registroelettronico.com/mastercom/register_manager.php?user={user}&password={password}"
	subjectsUrl = "https://rosmini-tn.registroelettronico.com/mastercom/register_manager.php?action=get_subjects&page=1&start=0&limit=25"
	marksUrl = "https://rosmini-tn.registroelettronico.com/mastercom/register_manager.php?action=get_grades_subject&page=1&start=0&limit=100&id_materia={subjectId}"


	class Subject:
		def __init__(self, data):
			self.name = data["nome"]
			self.id = data["id"]
			self.marks = None
		def __repr__(self):
			return self.name
		
		def marksAverage(self):
			sum = 0
			for m in self.marks:
				sum += m.value			
			return sum / len(self.marks)

	class Mark:
		class Type(enum.Enum):
			written = 0,
			oral = 1,
			practical = 2

			@classmethod
			def parse(cls, string):
				if string == "Scritto":
					return cls.written
				elif string == "Orale":
					return cls.oral
				elif string == "Pratico":
					return cls.practical

		def __init__(self, data):
			self.value = float(data["valore"])
			self.type = self.Type.parse(data["tipo"])
			self.date = datetime.strptime(data["data"][0:-15], "%a, %d %b %Y")
			self.note = data["note"]
		def __repr__(self):
			return str(self.value)

	
	def _authenticate(self):
		initialHeaders = {
			"Host": "rosmini-tn.registroelettronico.com",
			"User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:67.0) Gecko/20100101 Firefox/67.0",
			"Accept": "*/*",
			"Accept-Language": "en-US,en;q=0.5",
			"Accept-Encoding": "gzip",
			"Referer": "https://rosmini-tn.registroelettronico.com/quaderno/",
			"DNT": "1",
			"Connection": "keep-alive",
		}

		request = urllib.request.Request(self.authenticateUrl.format(user=self.user, password=self.password), headers=initialHeaders)
		response = urllib.request.urlopen(request)
		data = zlib.decompress(response.read(), 16+zlib.MAX_WBITS) # data is gzip compressed

		jsonData = json.loads(data)
		self.userFullName = jsonData["result"]["full_name"].title()

		# add cookie to headers to authenticate
		self.headers = initialHeaders
		self.headers["Cookie"] = str(response.headers).split("\n")[6][12:-8]

	def __init__(self, user, password):
		self.user = user
		self.password = password
		self._authenticate()
	

	def _fetchJsonUrl(self, url):
		request = urllib.request.Request(url, headers=self.headers)
		response = urllib.request.urlopen(request)

		if response.headers["Content-Encoding"] == "gzip":
			data = zlib.decompress(response.read(), 16+zlib.MAX_WBITS) # data is gzip compressed
		else:
			data = response.read()

		return json.loads(data)

	def loadSubjects(self):
		self.subjects = [self.Subject(s)
			for s in self._fetchJsonUrl(self.subjectsUrl)["result"]]
	

	def _loadMarksIndex(self, subjectIndex):
		url = self.marksUrl.format(subjectId=self.subjects[subjectIndex].id)
		self.subjects[subjectIndex].marks = [self.Mark(m)
			for m in self._fetchJsonUrl(url)["result"]]

	def loadMarks(self, subjectName):
		for s in range(len(self.subjects)):
			if self.subjects[s].name == subjectName:
				index = s
		self._loadMarksIndex(index)
	
	def loadAllMarks(self):
		for index in range(len(self.subjects)):
			self._loadMarksIndex(index)

	
	def print(self):
		print(self.userFullName + "'s workbook:")
		for s in self.subjects:
			print("@ " + s.name)
			if len(s.marks) == 0:
				print("   ():")
			else:
				print("   (" + str(round(s.marksAverage()*100)/100) + "):", *s.marks)