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

path = os.path.join('CONDOR', 'timing_TSlength', 'res')

plt.rcParams.update({'font.size': 20, 'lines.linewidth': 3})
plt.rcParams['xtick.labelsize'] = 20
plt.rcParams['ytick.labelsize'] = 20

files = [f for f in listdir(path) if isfile(join(path, f))]
print len(files)

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
            out[l[0]] = out.get(l[0], [])+[l[1]]
            out_SP[l[0]] = out_SP.get(l[0], [])+[l[2]]
            out_BS[l[0]] = out_BS.get(l[0], [])+[l[3]]

    f.close()


x = sorted(out.keys())
total = np.array([out[i][0] for i in sorted(out.keys())])
bins = np.linspace(min(x), max(x), 101)
digitized = np.digitize(x, bins)
bin_means = [total[digitized == i].mean() for i in range(1, len(bins))]
print len(bins), len(bin_means)

x_bins = [0.5*(bins[ind]+bins[ind+1]) for ind in xrange(len(bins)-1)]

#plt.plot(x_bins, bin_means)
plt.scatter(x_bins, bin_means, s=50)
plt.xlim(0, max(x_bins))
plt.ylim(0, max(bin_means)+10.0)


bins = np.linspace(min(x), max(x), 51)
digitized = np.digitize(x, bins)
bin_means = [total[digitized == i].mean() for i in range(1, len(bins))]
x_bins = [0.5*(bins[ind]+bins[ind+1]) for ind in xrange(len(bins)-1)]

fit = np.polyfit(x_bins, bin_means, 1)
fit_fn = np.poly1d(fit)
print fit_fn
plt.plot(x_bins, fit_fn(x_bins), ':r')

print x_bins
print bin_means

#
#
#fit = np.polyfit(x_bins, bin_means, 2)
#fit_fn = np.poly1d(fit)
#print fit_fn
#plt.plot(x_bins, fit_fn(x_bins), '--r')

plt.ylabel('running time (sec)', fontsize=27)
plt.xlabel('number of time-stamped edges', fontsize=27)
#plt.ylim(ymax = 1.0, ymin = 0.0)
#plt.legend(['CulT', 'reports', 'baseline'], loc = 3, fontsize=27)
plt.tight_layout()
name = plt.savefig('timing_TSlength_avg_scatter.pdf')
exit()
plt.show()
exit()

#out = {k: np.mean(v) for k,v in out.iteritems()}
#out_SP = {k: np.mean(v) for k,v in out_SP.iteritems()}
#out_BS = {k: np.mean(v) for k,v in out_BS.iteritems()}




x = sorted(out.keys())
#out = {k: np.mean(v) for k,v in out.iteritems()}
#print out[x[0]]


total = [out[i] for i in sorted(out.keys())]
#time_SP = [out_SP[i] for i in sorted(out_SP.keys())]
#time_BS = [out_BS[i] for i in sorted(out_BS.keys())]

#plt.boxplot(total)
#fit = np.polyfit(x, total, 1)
#fit_fn = np.poly1d(fit)
#plt.rcParams.update({'font.size': 15, 'lines.linewidth': 3})

plt.plot(x, total)
#plt.scatter(x, total)

#plt.plot(x, fit_fn(x), '--r')
#plt.xlim(0, max(x))
#plt.ylim(0, max(total))
#plt.plot(x, time_SP)
#plt.plot(x, time_BS)

#plt.legend(['MCC', 'MCC reports', 'MCC BL'], loc = 3)
plt.ylabel('running time (sec)')
plt.xlabel('number of time-stamped edges')
plt.tight_layout()
name = plt.savefig('timing_TSlength_box.pdf')
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
