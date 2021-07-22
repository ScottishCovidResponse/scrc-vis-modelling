__author__ = 'Polina'
from datetime import datetime


#Input format:
#Timestamp \t nodeid1 \t nodeid2 \t id1Status \t id2Status \t id1Report \t
#idXState: 1 is infected at time t, 0 otherwise.
#id1Report: 1 means that the node1 has been reported as active at this time
def readRealFile(filepath):
    TS = []
    with open(filepath,'r') as fd:
        for line in fd.readlines():
            if line[0] == '#':
                break
            line = line.strip()
            items = line.split('\t')
            tstamp = datetime.strptime(items[0], '%Y-%m-%d %H:%M:%S')
            ##print items
            n1, n2 = int(items[1]), int(items[2])
            active1, active2 = int(items[3]), int(items[4])
            report1, report2 = int(items[5]), int(items[6])
            
            #Unless we are evaluating an experiment data these should be equal for real data
            assert active1 == report1
            assert active2 == report2

            if n1 == n2:
                continue
            TS.append([tstamp] + [n1, n2]+ [active1,active2] + [report1,report2])

    fd.close()
    return TS;
	
