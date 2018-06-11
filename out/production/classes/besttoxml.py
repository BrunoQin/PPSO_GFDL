import xlwt

i1 = 4
i2 = 10
i3 = 28

wb = xlwt.Workbook()
ws = wb.add_sheet('A Test Sheet')

won = True
best = 0
bestI = -1
for line in open("/Users/Bruno/Desktop/best.txt"):
	if won:
		index=i=tempI=iq=0
		if line[5]=='s':
			tempI = int(line[4])
			if line[11]=='-':
				index = int(line[iq+i2])
				i = iq+i3
			else:								#if line[11]!='-'&&line[12]=='-'
				index = int(line[iq+i2:iq+i2+2])
				i = iq+i3+1
		else:
			tempI = int(line[4:6])
			iq=1
			if line[12]=='-':
				index = int(line[iq+i2])
				i = iq+i3
			else:								#if line[11]!='-'&&line[12]=='-'
				index = int(line[iq+i2:iq+i2+2])
				i = iq+i3+1

		temp = float(line[i:len(line)-1])
		#tempI = int(line[4])
		ws.write(index,tempI,temp)
		if temp > best:
			best = temp
			bestI = index
		if index == 29:
			ws.write(32,tempI,bestI+1)
			ws.write(33,tempI,best)
			print("kkkk",bestI)
			print("bbbbb",best)
			best = 0
			bestI = -1
		won = False
	else:
		won = True
#	print(line[2])



wb.save('/Users/Bruno/Desktop/best.xls')
print("kkkk")
print("bbbbb")
