import os
import sys

#from utils.generator import *
from utils.generator_noise import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.visualization import *
from utils.paths import *
from utils.postprocessing import *
from utils.get_path_stats import *
import pickle

M = 30
N = 100
srcN = 5
infectionP = 0.3
reportingP = 0.5
recoveringP = 0.0
K = srcN
#dataset = str(sys.argv[1])

dataset = 'facebook'
#dataset = 'twitter'
#dataset = 'students'
#dataset = 'tumblr'
#dataset = 'enron'

path = os.path.join('CONDOR', 'timing_infSize', 'res')

plt.rcParams.update({'font.size': 20, 'lines.linewidth': 3})
plt.rcParams['xtick.labelsize'] = 20
plt.rcParams['ytick.labelsize'] = 20

files = [f for f in listdir(path) if isfile(join(path, f))]
print files
out = {}
out_SP = {}
out_BS = {}
for i in files:
    fullname = os.path.join(path, i)
    #print fullname
    with open(fullname, 'r') as f:
        lines = f.readlines()

    f.close()

    for j in xrange(len(lines)):
        if 'timing' in lines[j]:
            l = map(float, lines[j+1].split(' '))
            k = l[0]
            out[k] = out.get(k, [])+[l[1]]
            out_SP[k] = out_SP.get(k, [])+[l[2]]
            out_BS[k] = out_BS.get(k, [])+[l[3]]


x = sorted(out.keys())

out = {k: np.mean(v) for k,v in out.iteritems()}
out_SP = {k: np.mean(v) for k,v in out_SP.iteritems()}
out_BS = {k: np.mean(v) for k,v in out_BS.iteritems()}

total = [out[i] for i in sorted(out.keys())]
time_SP = [out_SP[i] for i in sorted(out_SP.keys())]
time_BS = [out_BS[i] for i in sorted(out_BS.keys())]

print x, total
plt.plot(x, total)
#plt.plot(x, time_SP)
#plt.plot(x, time_BS)
#plt.legend(['total', 'SP', 'BS'], loc = 2)
#plt.show()
#exit()
#plt.legend(['MCC', 'MCC reports', 'MCC BL'], loc = 3)
plt.ylabel('running time (sec)', fontsize=27)
plt.xlabel('fraction of infected nodes', fontsize=27)
plt.tight_layout()
name = plt.savefig('timing_inf_fraction_avg.pdf')
exit()



#TS, snapshots, G = readFile('generated.txt')

TS, G, infection_order, infection_track, seeds, nodes = get_noisy_TS_leaves_with_P(type = dataset, n = N, inf_fraction = 0.5, seeds_num = srcN, noise = noise, reportP = reportingP, model = model)
K = len(seeds)

#H =  get_infection_tree(TS)
sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources_noise(TS, mode = 'all')
print len(sinks), len(sources)

start = time.time()
SP = shortestPath1(TS, sources, sinks, immuned, unreported)
SPtime = time.time() - start

start = time.time()
cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)
BStime = time.time() - start

print 'timing:'
print len(TS), SPtime+BStime, SPtime, BStime
