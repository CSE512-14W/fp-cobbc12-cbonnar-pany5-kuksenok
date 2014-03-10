import csv

f = open('result.csv', 'r')
output_file = open('clean.csv', 'w')
#REF-- 0:lat, 1:lon, 2:dev, 3:route 4: time, 5: day, 6: hr, 7: min
#format: data[day_id][elem] 
#day_id: "11 16 2011" == 0, "11 17 2011" == 1, "11 18 2011" == 2, "11 21 2011" == 3
#"2 2 2012" == 4, "2 3 2012" == 5, "2 4 2012" == 6, "2 7 2012" == 7
#data[day_id][0] = [lat, long]
#data[day_id][1] = schedDev
#data[day_id][2] = [hour, min]
#data[day_id][3] = route_short_name

prevLine = f.readline()
b = []
b = prevLine.split(',')
count = 0
for l in f:
	l.replace("\"", "")
	a = l.split(',')
	if (a[0] == b[0] and a[1] == b[1] and a[5] == b[5] and a[6] == b[6] and abs(int(a[7]) - int(b[7])) > 0 and abs(int(a[7]) - int(b[7])) < 20 and a[3]==b[3]): continue
	output_file.write(l)	
	b = a
	count+=1

print count


