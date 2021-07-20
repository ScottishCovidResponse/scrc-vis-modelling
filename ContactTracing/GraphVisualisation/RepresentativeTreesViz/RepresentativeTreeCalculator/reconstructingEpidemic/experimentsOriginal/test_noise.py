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
reportingP = 0.1
recoveringP = 0.0
K = srcN
#dataset = str(sys.argv[1])

dataset = 'facebook'
#dataset = 'twitter'
#dataset = 'students'
#dataset = 'tumblr'
#dataset = 'enron'

p = 4.0/N
real = False
noise = 500
model = 'SI'

#TS, snapshots, G = readFile('generated.txt')

TS, G, infection_order, infection_track, seeds, nodes = get_noisy_TS_leaves_with_P(type = dataset, n = N, inf_fraction = 0.5, seeds_num = srcN, noise = noise, reportP = 1.0, model = model)
K = 3

#H =  get_infection_tree(TS)
sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources_noise(TS, mode = 'all')
print len(sinks), len(sources)

SP = shortestPath1(TS, sources, sinks, immuned, unreported)

cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)
gt_cost, gt_interactions, gt_causality = get_GT_cost(TS, sources, sinks, unreported)
print 'cost of GT', gt_cost
print cover
print output_paths


out_cost, out_interactions, out_causality = get_out_cost(output_paths, sources, sinks, unreported)
print 'cost of our solution', out_cost

print 'correct interactions', len(set(gt_interactions).intersection(set(out_interactions)))
print 'intesection precision', np.divide(1.0*len(set(gt_interactions).intersection(set(out_interactions))), len(set(gt_interactions)))
print 'intesection recall', np.divide(1.0*len(set(gt_interactions).intersection(set(out_interactions))), len(set(out_interactions)))

print 'correct of causality', len(set(gt_causality).intersection(set(out_causality)))
print 'causality precision', np.divide(1.0*len(set(gt_causality).intersection(set(out_causality))), len(set(gt_causality)))
print 'causality recall', np.divide(1.0*len(set(gt_causality).intersection(set(out_causality))), len(set(out_causality)))

#get_infection_paths_noise(TS, output_paths, cover.keys())
N = G.number_of_nodes()
M = len(TS)

ticksN = 100
GT_snapshots, moment_of_infection, _, _ = get_snapshots(TS, ticksN, N)

#path_tau, path_lengths = validatePaths(output_paths, moment_of_infection, set(sinks.keys()))
#print 'path-wise tau', np.nanmean(path_tau), stats.nanmedian(path_tau)
#print 'path length', np.nanmean(path_lengths), stats.nanmedian(path_lengths)
#exit()


folder = time.strftime('test_' + dataset + "_%Y%m%d-%H%M%S")
os.mkdir(folder)

#added_infections, counters, gt_uninf_neighbors, out_uninf_neighbors, gt_inf_neighbors, out_inf_neighbors = postPr(TS, output_paths, 0.3)


print 'accuracy'
draw = False
ticksN = 100

#GT_snapshots, _, moment_of_infection, _, _ = get_snapshots(TS, ticksN, N)
lb_snapshots = get_lb_snapshots(sources, immuned, reported, GT_snapshots)
#ub_snapshots_one = get_ub_snapshots(TS, GT_snapshots)
#print len()
ub_snapshots_cascade, ub_interactions, ub_causality = get_ub_snapshots(TS, GT_snapshots, sinks)

print 'compare order for UB and GT:'
print len(set(gt_interactions).intersection(set(ub_interactions)))
print set(gt_causality).intersection(set(ub_causality))

#print ub_interactions
#print ub_causality
#ub_snapshots_cascade = get_ub_snapshots(TS, GT_snapshots)
output_snapshots, _ = get_output_snapshots_no_recov_pred(output_paths, GT_snapshots, immuned)
print 'how far in time'
slack, tau = how_far_intime(output_paths, moment_of_infection)
print 'total tau', tau
print 'slack', np.nanmean(slack), stats.nanmedian(slack)


pred_recover = False
prec_infected, recall_infected, prec_recovered, recall_recovered, abs_values_tp, gt_positive, abs_values_p, set_nodes, set_nodes_gt, MCC, F1 \
    = snapshot_accuracy(GT_snapshots, output_snapshots, output_paths, immuned, sources, reported, TS, G, N, 'main', pred_recover = pred_recover,draw = draw, folder = folder)
prec_lb_infected, recall_lb_infected, prec_lb_recovered, recall_lb_recovered, abs_values_lb_tp, _,_,_,_,MCC_lb,F1_lb \
    = snapshot_accuracy(GT_snapshots, lb_snapshots, output_paths, immuned, sources, reported, TS, G, N,'lb', pred_recover = pred_recover, draw = draw, folder = folder)
prec_ub_infected, recall_ub_infected, prec_ub_recovered, recall_ub_recovered, abs_values_ub_tp,_,abs_values_ub_p,_,_,MCC_ub,F1_ub \
    = snapshot_accuracy(GT_snapshots, ub_snapshots_cascade, output_paths, immuned, sources, reported, TS, G, N, 'ub', pred_recover = pred_recover, draw = draw, folder = folder)
degrees_out = [G.degree(i) for i in set_nodes[-1]]
degrees_gt = [G.degree(i) for i in set_nodes_gt[-1]]

print stats.nanmedian(prec_infected), stats.nanmedian(recall_infected), stats.nanmedian(F1)
print stats.nanmedian(prec_lb_infected), stats.nanmedian(recall_lb_infected), stats.nanmedian(F1_lb)
print stats.nanmedian(prec_ub_infected), stats.nanmedian(recall_ub_infected), stats.nanmedian(F1_ub)

#exit()

#prec_infected_add, recall_infected_add, prec_recovered_add, recall_recovered_add, abs_values_tp_add, gt_positive_add, abs_values_p_add, set_nodes_add, set_nodes_gt_add , MCC_added,F1_added\
#    = snapshot_accuracy(GT_snapshots, joined_snapshots, output_paths, immuned, sources, reported, TS, G, N, 'main', pred_recover = pred_recover,draw = draw, folder = folder)
#degrees_out_add = [G.degree(i) for i in set_nodes_add[-1]]


title = str(N) + ' nodes; ' + str(M) + ' timestamps; ' + str(infectionP) + ' inf. prob.; ' + str(reportingP) + ' report prob.; ' + str(srcN) + ' src'
plt.figure('absolute values')
plt.plot(abs_values_tp, 'k-')
#plt.plot(abs_values_tp_add, c='gray', ls='-')
plt.plot(abs_values_p, 'k:')
#plt.plot(abs_values_p_add, c='gray', ls=':')
#plt.plot(mcc)
plt.plot(abs_values_lb_tp, 'r--')
plt.plot(gt_positive, 'r-')
plt.plot(abs_values_ub_tp, 'b-')
plt.plot(abs_values_ub_p, 'b:')

plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['TP', 'TP added', 'P', 'P added', 'reports', 'GT', 'TP cascade', 'P cascade'], loc = 3)
plt.legend(['TP', 'P', 'reports', 'GT', 'TP cascade', 'P cascade'], loc = 3)

plt.title(title)
#plt.savefig('my.pdf')
#plt.show()
name = 'positive' + '_'.join([str(dataset), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
#print 'TP' + name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
name = plt.savefig(name)

title = str(N) + ' nodes; ' + str(M) + ' timestamps; ' + str(infectionP) + ' inf. prob.; ' + str(reportingP) + ' report prob.; ' + str(srcN) + ' src'
plt.figure('accuracy')
plt.plot(prec_infected, 'k-')
#plt.plot(prec_infected_add,  c='grey', ls='-')

plt.plot(prec_lb_infected, 'k--')
plt.plot(prec_ub_infected, 'k:')

plt.plot(recall_infected,'r-')
#plt.plot(recall_infected_add, c='pink', ls='-')
plt.plot(recall_lb_infected, 'r--')
plt.plot(recall_ub_infected, 'r:')
#plt.plot(mcc_lb)
plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = min(mcc))
plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['Prec', 'Prec added', 'Prec reported only', 'Prec fan-out', 'Recall',  'Recall added', 'Recall reported only', 'Recall fan-out'], loc = 3)
plt.legend(['Prec', 'Prec reported only', 'Prec fan-out', 'Recall', 'Recall reported only', 'Recall fan-out'], loc = 3)

#plt.legend(['P', 'R', 'P_lb', 'R_lb', 'P_ub', 'R_ub'])
#plt.legend(['P', 'R', 'MCC', 'P_lb', 'R_lb'])
plt.title(title)
name = 'acc_'.join([str(dataset),str(N),str(M),str(int(infectionP*10)),str(int(reportingP*10)),str(srcN)])
print name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)

title = str(N) + ' nodes; ' + str(M) + ' timestamps; ' + str(infectionP) + ' inf. prob.; ' + str(reportingP) + ' report prob.; ' + str(srcN) + ' src'
plt.figure('MCC')
plt.plot(MCC, 'k-')
#plt.plot(MCC_added, 'b-')
plt.plot(MCC_lb, 'k--')
plt.plot(MCC_ub, 'k:')

plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = min(mcc))
plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['MCC', 'MCC added', 'MCC reports', 'MCC cascade'], loc = 3)
plt.legend(['MCC', 'MCC reports', 'MCC cascade'], loc = 3)
plt.title(title)
name = 'mcc_'.join([str(dataset),str(N),str(M),str(int(infectionP*10)),str(int(reportingP*10)),str(srcN)])
print name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)


name = 'F1_'.join([str(dataset),str(N),str(M),str(int(infectionP*10)),str(int(reportingP*10)),str(srcN)])
print name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)

title = str(N) + ' nodes; ' + str(M) + ' timestamps; ' + str(infectionP) + ' inf. prob.; ' + str(reportingP) + ' report prob.; ' + str(srcN) + ' src'
plt.figure('F1')

plt.plot(F1, 'k-')
#plt.plot(F1_added, 'b-')
plt.plot(F1_lb, 'k--')
plt.plot(F1_ub, 'k:')

plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = min(mcc))
plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['F1', 'F1 added', 'F1 reports', 'F1 cascade'], loc = 3)
plt.legend(['F1', 'F1 reports', 'F1 cascade'], loc = 3)
plt.title(title)
name = 'F1_'.join([str(dataset),str(N),str(M),str(int(infectionP*10)),str(int(reportingP*10)),str(srcN)])
print name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)


#pickle.dump([output_paths, prec_infected, recall_infected, prec_lb_infected, recall_lb_infected, prec_ub_infected, recall_ub_infected], open( "save.p", "wb" ) )
#visualize(folder)
plt.show()
