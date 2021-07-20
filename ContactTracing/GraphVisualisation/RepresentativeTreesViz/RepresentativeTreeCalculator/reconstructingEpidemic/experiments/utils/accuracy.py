__author__ = 'Polina'
import copy
import numpy as np
import operator
import scipy.stats as stats

def how_far_intime(paths, moment_of_infection, mode = 'abs'):
    res = []
    out_moment_of_infection = {}
    for p in paths:
        for step in p:
            if step[1] in out_moment_of_infection:
                out_moment_of_infection[step[1]] = min(out_moment_of_infection[step[1]], step[0])
            else:
                out_moment_of_infection[step[1]] = step[0]

            if step[2] in out_moment_of_infection:
                out_moment_of_infection[step[2]] = min(out_moment_of_infection[step[2]], step[0])
            else:
                out_moment_of_infection[step[2]] = step[0]
    sorted_out = [i[0] for i in sorted(out_moment_of_infection.items(), key=operator.itemgetter(1)) if i[0] in moment_of_infection]
    sorted_gt = [i[0] for i in sorted(moment_of_infection.items(), key=operator.itemgetter(1)) if i[0] in out_moment_of_infection]

    for k, v in out_moment_of_infection.items():
        if k in moment_of_infection:
            if v > moment_of_infection[k]:
                t = (v - moment_of_infection[k]).total_seconds()
            else:
                t = -(moment_of_infection[k] - v).total_seconds()
                if mode == 'abs':
                    t = np.abs(t)
            res.append(t)
    #return res, stats.kendalltau(sorted_gt, sorted_out), stats.pearsonr(sorted_gt, sorted_out)
    try:
        tau = stats.kendalltau(sorted_gt, sorted_out)
    except:
        tau = 0.0
    return res, tau

def get_output_snapshots_no_recov_pred(pths, snapshots, immuned):# pths - list of lists
    output_infected = set()
    output_recovered = set()
    output_seeds = set()
    found_seeds = {}
    output_snapshots = {}
    output = {}
    output_interactions = set()
    node_activity = {}

    for p in pths:
        for step in p:
            output_interactions.add(step)
            # if step[1] in immuned:
            #     node_activity[step[1]] = node_activity.get(step[1], []) + [step[0]]
            # if step[2] in immuned:
            #     node_activity[step[2]] = node_activity.get(step[2], []) + [step[0]]
        if p:
            found_seeds[p[0][1]] = min(p[0][0], found_seeds.get(p[0][1], p[0][0]))


    # for node in node_activity:
    #     node_activity[node].sort()

    sorted_output = sorted(list(output_interactions))

    snapshots_time = sorted(snapshots.keys())
    iter_output = 0
    to_recover = set()
    for i in range(len(snapshots_time)):
        t1 = snapshots_time[i]
        #output_recovered.update(to_recover)
        #output_infected.difference_update(output_recovered)
        #output_seeds.difference_update(output_recovered)
        while iter_output < len(sorted_output) and sorted_output[iter_output][0] <= t1:
            #output_recovered.update(to_recover)
            #output_infected.difference_update(output_recovered)
            #output_seeds.difference_update(output_recovered)

            n1 = sorted_output[iter_output][1]
            n2 = sorted_output[iter_output][2]
            output_infected.add(n1)
            output_infected.add(n2)

            if n1 in immuned:
                if sorted_output[iter_output][0] >= immuned[n1]:
                    output_recovered.add(n1)
                    if n1 in output_infected:
                        output_infected.remove(n1)
                    if n1 in output_seeds:
                        output_seeds.remove(n1)

            if n2 in immuned:
                if sorted_output[iter_output][0] >= immuned[n2]:
                    output_recovered.add(n2)
                    if n2 in output_infected:
                        output_infected.remove(n2)
                    if n2 in output_seeds:
                        output_seeds.remove(n2)

            if n1 in found_seeds:
                if sorted_output[iter_output][0] >= found_seeds[n1]:
                    output_seeds.add(n1)

            if n2 in found_seeds:
                if sorted_output[iter_output][0] >= found_seeds[n2]:
                    output_seeds.add(n2)
            iter_output += 1

            # if n1 in found_seeds.keys() and found_seeds[n1] <= t1:
            #     output_seeds.add(n1)

        output_snapshots['seeds'] = copy.deepcopy(output_seeds)
        #if iter_output >= len(sorted_output):
        #    output_recovered.update(to_recover)
        #    output_infected.difference_update(output_recovered)
        output_snapshots['infected'] = copy.deepcopy(output_infected)
        output_snapshots['recovered'] = copy.deepcopy(output_recovered)
        output[t1] = copy.deepcopy(output_snapshots)
    return output, found_seeds


def get_output_snapshots(pths, snapshots, immuned):# pths - list of lists
    output_infected = set()
    output_recovered = set()
    output_seeds = set()
    found_seeds = set()
    output_snapshots = {}
    output = {}
    output_interactions = set()
    node_activity = {}


    for p in pths:
        for step in p:
            output_interactions.add(step)
            if step[1] in immuned:
                node_activity[step[1]] = node_activity.get(step[1], []) + [step[0]]
            if step[2] in immuned:
                node_activity[step[2]] = node_activity.get(step[2], []) + [step[0]]
        #found_seeds[p[0][1]] = found_seeds[p[0][0]]
        found_seeds[p[0][1]] = min(p[0][0], found_seeds.get(p[0][1], p[0][0]))


    for node in node_activity:
        node_activity[node].sort()

    sorted_output = sorted(list(output_interactions))

    snapshots_time = sorted(snapshots.keys())
    iter_output = 0
    to_recover = set()
    for i in xrange(len(snapshots_time)):
        t1 = snapshots_time[i]
        output_recovered.update(to_recover)
        output_infected.difference_update(output_recovered)
        while iter_output < len(sorted_output) and sorted_output[iter_output][0] <= t1:
            output_recovered.update(to_recover)
            output_infected.difference_update(output_recovered)

            n1 = sorted_output[iter_output][1]
            n2 = sorted_output[iter_output][2]
            output_infected.add(n1)
            output_infected.add(n2)
            if n1 in immuned:
                if sorted_output[iter_output][0] == node_activity[n1][-1]:
                    to_recover.add(n1)
                if sorted_output[iter_output][0] == immuned[n1]:
                    output_recovered.add(n1)
                    output_infected.remove(n1)
                    if n1 in output_seeds:
                        output_seeds.remove(n1)

            if n2 in immuned:
                if sorted_output[iter_output][0] == node_activity[n2][-1]:
                    to_recover.add(n2)
                if sorted_output[iter_output][0] == immuned[n2]:
                    output_recovered.add(n2)
                    output_infected.remove(n2)
                    if n2 in output_seeds:
                        output_seeds.remove(n2)
            iter_output += 1


        if n1 in found_seeds.keys() and found_seeds[n1] <= t1:
            output_seeds.add(n1)

        output_snapshots['seeds'] = copy.deepcopy(output_seeds)
        #if iter_output >= len(sorted_output):
        #    output_recovered.update(to_recover)
        #    output_infected.difference_update(output_recovered)
        output_snapshots['infected'] = copy.deepcopy(output_infected)
        output_snapshots['recovered'] = copy.deepcopy(output_recovered)
        output[t1] = copy.deepcopy(output_snapshots)
    return output



def snapshot_accuracy(GT_snapshots, output_snapshots, pths, immuned, sources, reported, TS, G, num_nodes, mode = 'main', pred_recover = False, draw = False, folder = ''):

    precision_infected = []
    recall_infected = []
    abs_values_TP = []
    gt_values = []
    abs_values_T = []
    set_T = []
    set_gt_T = []
    MCC = []
    F1 = []
    for k in sorted(GT_snapshots.keys()):
        #print k, output_snapshots
        TP_infected = float(len(GT_snapshots[k]['infected'] & output_snapshots[k]['infected']))
        TN_ = float(num_nodes - len(GT_snapshots[k]['infected'] | output_snapshots[k]['infected']))
        FP_ = float(len(output_snapshots[k]['infected'] - GT_snapshots[k]['infected']))
        FN_ = float(len(GT_snapshots[k]['infected'] - output_snapshots[k]['infected']))

        #print (num_nodes, len(GT_snapshots[k]['infected']), len(output_snapshots[k]['infected']))
        #print (len(GT_snapshots[k]['infected'] | output_snapshots[k]['infected']))

        #float(num_nodes - len(GT_snapshots[k]['infected'] | output_snapshots[k]['infected']))

        precision_infected.append(np.divide(TP_infected, len(output_snapshots[k]['infected'])))
        recall_infected.append(np.divide(TP_infected, len(GT_snapshots[k]['infected'])))

        abs_values_TP.append(TP_infected)
        gt_values.append(len(GT_snapshots[k]['infected']))
        abs_values_T.append(len(output_snapshots[k]['infected']))
        set_T.append(output_snapshots[k]['infected'])
        set_gt_T.append(GT_snapshots[k]['infected'])
        #print ('accuracies',k, mode, TP_infected, FP_, TP_infected, FN_, TN_, FP_, TN_, FN_)

        #Can result in divide by 0 which gives a warning. Warning can be simply ignored.
        MCC.append(np.divide((TP_infected*TN_ - FP_*FN_), np.sqrt((TP_infected+FP_)*(TP_infected+FN_)*(TN_+FP_)*(TN_+FN_))))

        F1.append(np.divide(2.0*precision_infected[-1]*recall_infected[-1], (precision_infected[-1]+recall_infected[-1])))
        precision_recovered = []
    recall_recovered = []

    return precision_infected, recall_infected, precision_recovered, recall_recovered, abs_values_TP, gt_values, abs_values_T, set_T, set_gt_T, MCC, F1
