
using HDF5
using Plots
using DataTables
using Query
using Statistics

###################################################################
# Load in the data. 
###################################################################

datadir = "data"

# The time series data are labeled as <intervention>_<series>_time_series
# Then there is an array of arrays. Each leaf array represents a time series
# curve and the parent array is the set of all curves
function processds(ds)
  dsspt = split(ds, "_", limit=2)
  intervention = dsspt[1]
  series = chop(dsspt[2], tail=length("_time_series"))
  curves = map(x -> collect(x.data), read(rawdata[ds]["table"]))
  map((c) -> (intervention, series, c), curves)
end

# Load all time series data into one table that we can query later
# The HDF5 file provides all the data in a single file.
rawdata = h5open(datadir * "/" * "dynamics_time_series.h5", "r")
data = map(processds, names(rawdata));
data = collect(Iterators.flatten(data));
tsData = DataTable(Intervention=map(x -> x[1], data),
                   Series=map(x -> x[2], data),
                   Values=map(x -> x[3], data));

interventions = unique(tsData.Intervention);
series = unique(tsData.Series); # keep outside so we can keep things consistent between interventions

# Some of the series (e.g. trace_intervention) are for adding informational 
# elements
plotseries = series[10:12]

function maxseries(s)
  ts1 = tsData |>
    @filter(_.Series == s) |>
    @map(_.Values) |>
    collect
  maximum(map(maximum, ts1))
end

function mediancurve(s, i)
  ts1 = tsData |>
    @filter(_.Intervention == i) |> 
    @filter(_.Series == s) |>
    @map(_.Values)
  ts1 = collect(ts1)
  valmtx = hcat(ts1...)'
  median(valmtx, dims=1)'
end

function curvedata(s, i)
  ts1 = tsData |>
    @filter(_.Intervention == i) |> 
    @filter(_.Series == s) |>
    @map(_.Values)
  ts1
end

###################################################################
# Make the comparison interactive
###################################################################
using Interact, WebIO, Mux

# TODO: add show school and show intervention times
function advcreateplot(i, s, nCurves, alpha, showmedian) #, showschool, showintervention)
  x = 1:366
  maxy = maxseries(s)
  q = curvedata(s, i) |>
    @take(nCurves)
  ts1 = collect(q)
  p = plot(x, ts1, linecolor="black", linealpha=alpha, legend=false, 
                   linewidth=0.5, title=i, ylim=(0, maxy));
  if showmedian
    datamed = mediancurve(s, i);
    plot!(p, x, datamed, linecolor="red", legend=false, linewidth=2.0)
  end
  p
end

# Shows a curve of differences of the median values
function diffplot(s)
  x = 1:366
  ld = mediancurve(s, "Lockdown")
  base = mediancurve(s, "Base")
  diff = base .- ld
  plot(x, diff, linecolor="green", legend=false, xlabel="base - lockdown", title="Difference")
end

function cmpplot(s)
  x = 1:366
  ld = mediancurve(s, "Lockdown")
  base = mediancurve(s, "Base")
  diff = base .- ld
  plot(x, [base, ld], legend=true, label=["Base" "Lockdown"], title="Comparison")
end

function addschoolbars(p, s)
  x = 1:366
  maxy = maxseries(s)
  sd = curvedata("trace_school", "Base") |>
    @take(1) |>
    collect
  sd = sd[1] .- 1.0 # data is between 1 and 2
  sd = sd .* maxy
  plot!(p, x, sd, fillrange=[sd-sd, sd], fillalpha=0.1, fillcolor="blue", 
                  linewidth=0.0, linecolor=nothing)
end

function addinterventionbars(p, s)
  x = 1:366
  maxy = maxseries(s)
  sd = mediancurve("trace_intervention", "Lockdown") 
  sd = sd .- 1.0 # data is between 1 and 2
  sd = sd .* maxy
  plot!(p, x, sd, fillrange=[sd-sd, sd], fillalpha=0.1, fillcolor="purple",
                  linewidth=0.0, linecolor=nothing)
end

function iplot(s, nc, a, showmed, showschool, showintervention)
  #plots = map(i -> advcreateplot(i, s, nc, a, showmed), interventions)
  bp = advcreateplot("Base", s, nc, a, showmed)
  ldp = advcreateplot("Lockdown", s, nc, a, showmed)
  dp = diffplot(s)
  cp = cmpplot(s)
  if showschool
    bp = addschoolbars(bp, s)
    ldp = addschoolbars(ldp, s)
    dp = addschoolbars(dp, s)
    cp = addschoolbars(cp, s)
  end
  if showintervention
    ldp = addinterventionbars(ldp, s)
    dp = addinterventionbars(dp, s)
    cp = addinterventionbars(cp, s)
  end
  plot(bp, ldp, dp, cp, size=(800, 800))
  #plot(plots..., dp, link=:both, layout=(1,3), size=(1200,400))
end

alphaval = slider(range(0.0, 1.0, length=11), label="Alpha");
nCurves = widget(range(10, stop=100, step=10), label="Context curves");
showmedian = widget(true, label="Show median");
swidget = widget(series[1:length(series)-2], label="series"); # last 2 elmnts are school and intervention levels
showschool = widget(false, label="Show schools");
showintervention = widget(false, label="Show intervention");
controls = vbox(
  swidget,
  hbox(showschool, showintervention),
  hbox(nCurves, showmedian, alphaval)
);
ui = vbox(
  controls,
  map(iplot, swidget, nCurves, alphaval, showmedian, showschool, showintervention)
);


###################################################################
# Start a local web server to host the interactive page
###################################################################
port = 8082
println("serving on port $port")
wait(webio_serve(page("/", req -> ui), port)) # serve on a random port


