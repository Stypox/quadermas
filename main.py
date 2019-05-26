from workbook import MastercomWorkbook

user = input("User id: ")
password = input("Password: ")

workbook = MastercomWorkbook(user, password)

workbook.loadSubjects()
workbook.loadAllMarks()

print()
workbook.print()