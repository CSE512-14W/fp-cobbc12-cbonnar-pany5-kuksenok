import csv

f = open('clean.csv', 'r')
output_file = open('data1.js', 'w')
#REF-- 0:lat, 1:lon, 2:dev, 3:route 4: time, 5: day, 6: hr, 7: min
#format: data[day_id][elem] 
#day_id: "11 16 2011" == 0, "11 17 2011" == 1, "11 18 2011" == 2, "11 21 2011" == 3
#"2 2 2012" == 4, "2 3 2012" == 5, "2 4 2012" == 6, "2 7 2012" == 7
#data[day_id][0] = [lat, long]
#data[day_id][1] = schedDev
#data[day_id][2] = [hour, min]
#data[day_id][3] = route_short_name
data = []
lats = []
devs = []
times = []
routes = []
count = 0
data.append(lats)
data.append(devs)
data.append(times)
data.append(routes)

for l in f:
	a = l.split(',')
	if (a[5] == "2 7 2012"):
		b = []
		b.append(float(a[0]))
		b.append(float(a[1]))
		data[0].append(b)
		data[1].append(int(a[2]))
		time = []
		time.append(int(a[6]))
		time.append(int(a[7]))
		data[2].append(time)
		data[3].append(str(a[3]))

#initialize lat/lons
output_file.write('data[5][0] = [\n')
for item in data[0]:
	count+=1
	output_file.write(str(item))
	if (count != len(data[0])):
		output_file.write(',')

output_file.write('];\n\n')
print count
count = 0

#initialize devs
output_file.write('data[5][1] = [\n')
for item in data[1]:
	count+=1
	output_file.write(str(item))
	if (count != len(data[1])):
		output_file.write(',')

output_file.write('];\n')
print count
count = 0

#initialize times
output_file.write('data[5][2] = [\n')
for item in data[2]:
	count+=1
	output_file.write(str(item))
	if (count != len(data[2])):
		output_file.write(',')
print count
count = 0
output_file.write('];\n\n')

#initialize routes
output_file.write('data[5][3] = [\n')
for item in data[3]:
	count+=1
	output_file.write("\"" + str(item) + "\"")
	if (count != len(data[3])):
		output_file.write(',')
	
print count
output_file.write('];\n\n\n')


