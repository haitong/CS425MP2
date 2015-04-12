import os;

node_num = [4, 8, 12, 20, 30, 40, 100];

for node in node_num:
    for i in range(1):
        os.system("java Test "+`node`+" 1000 test."+`node`+".1000 "+`i`);


