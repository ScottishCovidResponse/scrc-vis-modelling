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
srcN = 10
infectionP = 0.3
reportingP = 0.5
recoveringP = 0.0
K = srcN
#dataset = str(sys.argv[1])

#dataset = 'facebookBig'
dataset = 'facebook'
#dataset = 'twitter'
#dataset = 'students'
#dataset = 'tumblr'
#dataset = 'enron'

p = 4.0/N
real = False
#N = int(sys.argv[1])
N = 100
noise = 100
model = 'SI'

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
