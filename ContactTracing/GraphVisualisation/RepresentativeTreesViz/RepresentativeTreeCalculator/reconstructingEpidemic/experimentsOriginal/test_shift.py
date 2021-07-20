import os
import sys
import uuid

#from utils.generator import *
from utils.generator_noise import *
from utils.generator_noise_misc import generateTS_final
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
# M = 10
# N = 10
# srcN = 2
infectionP = 0.3
reportingP = 0.5
recoveringP = 0.0
K = srcN

#type = 'PL'
#type = str(sys.argv[1])
type = 'facebook'
type = 'twitter'
type = 'students'
type = 'tumblr'
type = 'enron'
p = 4.0/N
real = False

#TS, snapshots, G = readFile('generated.txt')

I = 1
arr_abs_values_tp = [0] * I
arr_abs_values_p = [0] * I
arr_abs_values_lb_tp = [0] * I
arr_abs_gt_positive = [0] * I
arr_abs_values_ub_tp = [0] * I
arr_abs_values_ub_p = [0] * I

arr_prec_infected = [0] * I
arr_prec_lb_infected = [0] * I
arr_prec_ub_infected = [0] * I

arr_recall_infected = [0] * I
arr_recall_lb_infected = [0] * I
arr_recall_ub_infected = [0] * I

arr_MCC = [0] * I
arr_MCC_lb = [0] * I
arr_MCC_ub = [0] * I

arr_F1 = [0] * I
arr_F1_lb = [0] * I
arr_F1_ub = [0] * I

noise = 100
dt_sec = 100
#noise = 1
#dt_sec = 1
model = 'SI_1'
for iters in xrange(I):
    # TS, snapshots, G = generateTS_final(n = 100,
    #            p = 0.5,
    #            seed = 1.0,
    #            st = datetime(2000, 01, 01, 00, 00, 00),
    #            m = 1000,
    #            srcN = 5,
    #            reportingP = 0.3,
    #            infectionP = 0.7,
    #            recoveringP = 0.1,
    #            type = 'facebook',
    #            real = True,
    #            name = 'generated',
    #            percentage = 0.5)

    TS, G, infection_order, infection_track, seeds, nodes = get_noisy_TS_empty_reports(type = type, n = N, inf_fraction = 0.5, seeds_num = srcN, noise = noise, pad = dt_sec, model = model)
    e = np.linalg.eigvals(nx.to_numpy_matrix(G))
    #print("Largest eigenvalue:", max(e), 1.0/max(e))
    #exit()
    print seeds
    K = len(seeds)
    #K = 3
    #H =  get_infection_tree(TS)
    #sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources(TS, mode = 'all')
    sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources_shifted(TS, G = nx.Graph(), mode = 'all', dt_sec = dt_sec, rep_prob = reportingP)
    print len(sinks), len(sources), len(reported['infected'])
    #pickle.dump([TS, sources, sinks, immuned, unreported], open("save.p", "wb"))
    #TS, sources, sinks, immuned, unreported = pickle.load( open( "save.p", "rb" ) )
    #exit()
    #SP = shortestPath1(TS, sources, sinks, immuned, unreported)
    SP = shortestPath1(TS, sources, sinks, immuned, unreported)
    #K = srcN

    print 'K =', K
    #cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)

    cover, output_paths, cover_cost, legal_alpha = greedy_plot_KvsAlpha(SP, len(sinks), TS, ticks = 100)
    #exit()

    gt_cost, gt_interactions, gt_causality = get_GT_cost(TS, sources, sinks, unreported)
    print 'cost of GT', gt_cost
    print cover
    print output_paths


    out_cost, out_interactions, out_causality = get_out_cost(output_paths, sources, sinks, unreported)
    print 'cost of our solution', out_cost
    #exit()

    print 'correct interactions', len(set(gt_interactions).intersection(set(out_interactions)))
    print 'intesection precision', np.divide(1.0*len(set(gt_interactions).intersection(set(out_interactions))), len(set(gt_interactions)))
    print 'intesection recall', np.divide(1.0*len(set(gt_interactions).intersection(set(out_interactions))), len(set(out_interactions)))

    print 'correct of causality', len(set(gt_causality).intersection(set(out_causality)))
    print 'causality precision', np.divide(1.0*len(set(gt_causality).intersection(set(out_causality))), len(set(gt_causality)))
    print 'causality recall', np.divide(1.0*len(set(gt_causality).intersection(set(out_causality))), len(set(out_causality)))
    #exit()
    #get_infection_paths_noise(TS, output_paths, cover.keys())
    N = G.number_of_nodes()
    M = len(TS)

    ticksN = 100
    GT_snapshots, moment_of_infection, _, _ = get_snapshots(TS, ticksN, N)

    #path_tau, path_lengths = validatePaths(output_paths, moment_of_infection, set(sinks.keys()))
    #print 'path-wise tau', np.nanmean(path_tau), stats.nanmedian(path_tau)
    #print 'path length', np.nanmean(path_lengths), stats.nanmedian(path_lengths)
    #exit()


    #folder = time.strftime('test_' + type + "_%Y%m%d-%H%M%S")
    #os.mkdir(folder)

    #added_infections, counters, gt_uninf_neighbors, out_uninf_neighbors, gt_inf_neighbors, out_inf_neighbors = postPr(TS, output_paths, 0.3)

    print 'accuracy'
    draw = False
    ticksN = 100

    lb_snapshots = get_lb_snapshots(sources, immuned, reported, GT_snapshots)
    #ub_snapshots_cascade = get_ub_snapshots_cascade(TS, GT_snapshots, sinks)
    ub_snapshots_cascade, ub_interactions, ub_causality = get_ub_snapshots(TS, GT_snapshots, sinks)

    print 'intesection precision ub', np.divide(1.0*len(set(gt_interactions).intersection(set(ub_interactions))),len(set(gt_interactions)))
    print 'intesection recall ub', np.divide(1.0*len(set(gt_interactions).intersection(set(ub_interactions))),len(set(ub_interactions)))

    print 'causality precision ub', np.divide(1.0*len(set(gt_causality).intersection(set(ub_causality))),len(set(gt_causality)))
    print 'causality recall ub', np.divide(1.0*len(set(gt_causality).intersection(set(ub_causality))),len(set(ub_causality)))


    output_snapshots, found_seeds = get_output_snapshots_no_recov_pred(output_paths, GT_snapshots, immuned)
    print set(found_seeds.keys()).intersection(set(seeds))

    print 'how far in time'
    slack, tau = how_far_intime(output_paths, moment_of_infection)
    print 'total tau', tau
    print 'slack', np.nanmean(slack), stats.nanmedian(slack)

    folder = ''
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

    arr_abs_values_tp[iters] = abs_values_tp
    arr_abs_values_p[iters] = abs_values_p
    arr_abs_values_lb_tp[iters] = abs_values_lb_tp
    arr_abs_gt_positive[iters] = gt_positive
    arr_abs_values_ub_tp[iters] = abs_values_ub_tp
    arr_abs_values_ub_p[iters] = abs_values_ub_p

    arr_prec_infected[iters] = prec_infected
    arr_prec_lb_infected[iters] = prec_lb_infected
    arr_prec_ub_infected[iters] = prec_ub_infected

    arr_recall_infected[iters] = recall_infected
    arr_recall_lb_infected[iters] = recall_lb_infected
    arr_recall_ub_infected[iters] = recall_ub_infected

    arr_MCC[iters] = MCC
    arr_MCC_lb[iters] = MCC_lb
    arr_MCC_ub[iters] = MCC_ub

    arr_F1[iters] = F1
    arr_F1_lb[iters] = F1_lb
    arr_F1_ub[iters] = F1_ub

unique_out = type + '_' + str(uuid.uuid4())
pickle.dump([arr_abs_values_tp, arr_abs_values_p, arr_abs_values_lb_tp, arr_abs_gt_positive, arr_abs_values_ub_tp, arr_abs_values_ub_p,
             arr_prec_infected, arr_prec_lb_infected, arr_prec_ub_infected,
             arr_recall_infected, arr_recall_lb_infected, arr_recall_ub_infected,
             arr_MCC, arr_MCC_lb, arr_MCC_ub,
             arr_F1, arr_F1_lb, arr_F1_ub], open(unique_out+".p", "wb"))
exit()

title = 'absolute values'
plt.figure('absolute values')
plt.plot(np.nanmean(arr_abs_values_tp, axis=0), 'k-')
#plt.plot(abs_values_tp_add, c='gray', ls='-')
plt.plot(np.nanmean(arr_abs_values_p, axis=0), 'k:')
#plt.plot(abs_values_p_add, c='gray', ls=':')
#plt.plot(mcc)
plt.plot(np.nanmean(arr_abs_values_lb_tp, axis=0), 'r--')
plt.plot(np.nanmean(arr_abs_gt_positive, axis=0), 'r-')
plt.plot(np.nanmean(arr_abs_values_ub_tp, axis=0), 'b-')
plt.plot(np.nanmean(arr_abs_values_ub_p, axis=0), 'b:')

plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['TP', 'TP added', 'P', 'P added', 'reports', 'GT', 'TP cascade', 'P cascade'], loc = 3)
plt.legend(['TP output', 'output', 'reports', 'GT', 'TP BL', 'output BL'], loc = 3)

#plt.title(title)
#plt.savefig('my.pdf')
#plt.show()
name = 'positive' + '_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)), str(srcN)])
#print 'TP' + name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
name = plt.savefig(name)

#title = str(N) + ' nodes; ' + str(M) + ' timestamps; ' + str(infectionP) + ' inf. prob.; ' + str(reportingP) + ' report prob.; ' + str(srcN) + ' src'
title = 'accuracy'
plt.figure('accuracy')
plt.plot(np.nanmean(arr_prec_infected, axis=0), 'k-')
#plt.plot(prec_infected_add,  c='grey', ls='-')

plt.plot(np.nanmean(arr_prec_lb_infected, axis=0), 'k--')
plt.plot(np.nanmean(arr_prec_ub_infected, axis=0), 'k:')

plt.plot(np.nanmean(arr_recall_infected, axis=0),'r-')
#plt.plot(recall_infected_add, c='pink', ls='-')
plt.plot(np.nanmean(arr_recall_lb_infected, axis=0), 'r--')
plt.plot(np.nanmean(arr_recall_ub_infected, axis=0), 'r:')
#plt.plot(mcc_lb)
plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = min(mcc))
plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['Prec', 'Prec added', 'Prec reported only', 'Prec fan-out', 'Recall',  'Recall added', 'Recall reported only', 'Recall fan-out'], loc = 3)
plt.legend(['Prec', 'Prec reports', 'Prec BL', 'Recall', 'Recall reports', 'Recall BL'], loc = 3)

#plt.legend(['P', 'R', 'P_lb', 'R_lb', 'P_ub', 'R_ub'])
#plt.legend(['P', 'R', 'MCC', 'P_lb', 'R_lb'])
#plt.title(title)
name = 'acc_'.join([str(type),str(N),str(M),str(int(infectionP*10)),str(int(reportingP*10)),str(srcN)])
print name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)

#title = str(N) + ' nodes; ' + str(M) + ' timestamps; ' + str(infectionP) + ' inf. prob.; ' + str(reportingP) + ' report prob.; ' + str(srcN) + ' src'
title = 'MCC'
plt.figure('MCC')
plt.plot(np.nanmean(arr_MCC, axis=0), 'k-')
#plt.plot(MCC_added, 'b-')
plt.plot(np.nanmean(arr_MCC_lb, axis=0), 'k--')
plt.plot(np.nanmean(arr_MCC_ub, axis=0), 'k:')

plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = min(mcc))
plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['MCC', 'MCC added', 'MCC reports', 'MCC cascade'], loc = 3)
plt.legend(['MCC', 'MCC reports', 'MCC BL'], loc = 3)
#plt.title(title)
name = 'mcc_'.join([str(type),str(N),str(M),str(int(infectionP*10)),str(int(reportingP*10)),str(srcN)])
print name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)


name = 'F1_'.join([str(type),str(N),str(M),str(int(infectionP*10)),str(int(reportingP*10)),str(srcN)])
print name

name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)

title = str(N) + ' nodes; ' + str(M) + ' timestamps; ' + str(infectionP) + ' inf. prob.; ' + str(reportingP) + ' report prob.; ' + str(srcN) + ' src'
plt.figure('F1')

plt.plot(np.nanmean(arr_F1, axis=0), 'k-')
#plt.plot(F1_added, 'b-')
plt.plot(np.nanmean(arr_F1_lb, axis=0), 'k--')
plt.plot(np.nanmean(arr_F1_ub, axis=0), 'k:')

plt.xlabel('snapshots')
#plt.ylim(ymax = 1.01, ymin = min(mcc))
plt.ylim(ymax = 1.01, ymin = -0.1)
#plt.legend(['F1', 'F1 added', 'F1 reports', 'F1 cascade'], loc = 3)
plt.legend(['F1', 'F1 reports', 'F1 BL'], loc = 3)
#plt.title(title)
name = 'F1_'.join([str(type), str(N), str(M), str(int(infectionP*10)), str(int(reportingP*10)),str(srcN)])
print name
name = name+'_'+time.strftime("%Y%m%d-%H%M%S")
name = os.path.join(folder, name)
plt.tight_layout()
name = plt.savefig(name)

pickle.dump([output_paths, prec_infected, recall_infected, prec_lb_infected, recall_lb_infected, prec_ub_infected, recall_ub_infected], open( "save.p", "wb" ) )
#visualize(folder)
plt.show()
