// import { noneColorScheme } from "./ColorSchemes";

export class vars {
    //TODO: Currently a mess of global variables. Split and refactor after speedup of code

    public static initEditDistanceSliderVal = 6; //start the slider at 0
    public static currentEditDistance = this.initEditDistanceSliderVal; //Current edit distance
    public static maxParts = 7; //How many different parts we can have at maximum in the glyph.

    public static locationToVisualize = "All";

    public static currentLeftAttributeName = "None"; //What we are currently coloring the nodes by for the left sides of the glyphs
    public static currentLeftAttributeType = "None" //what the type is of the variable we are coloring for the left side of the glyphs
    public static currentLeftColorScheme: string[]; //the {maxParts} colors we are using in order.
    public static currentLeftColorSchemeValues: any[] = []; //The values used to determine which color to pick for the left side of the glyph. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in bin 10
    public static currentLeftAttributeBounds = [-Infinity, Infinity]; //Holds the values for the minimum and maximum value for the current integer attribute
    public static currentLeftDistributionSelection = ["All"]; //which levels of the distribution we are currently showing


    public static currentRightAttributeName = "None"; //What we are currently coloring the nodes by for the right sides of the glyphs
    public static currentRightAttributeType = "None" //what the type is of the variable we are coloring for the right side of the glyphs
    public static currentRightColorScheme:string[]; //the {maxParts} colors we are using in order.
    public static currentRightColorSchemeValues:any[] = []; //The values used to determine which color to pick for the right side of the glyhhp. For integers, these holds the upper bounds of the bin partitions. For categorical, these holds the 9 most frequent value names and "other" in bin 10
    public static currentRightAttributeBounds = [-Infinity, Infinity]; //Holds the values for the minimum and maximum value for the current integer attribute
    public static currentRightDistributionSelection = ["All"]; //which levels of the distribution we are currently showing

    public static normalizeComponentChart = false; //whether we normalize the bar chart against the total amount of nodes or not.



    // private june1 = 1622505600;
    // private december1 = 1638316800;

    public static startDate = 1622505600;
    public static endDate = 1638316800;

    //Visual variables
    //Variables for the tree visualization
    public static nodeBaseSize = 8; //radius of the node
    public static linkBaseSize = this.nodeBaseSize / 2; //Width of links
    public static verNodeSpace = this.nodeBaseSize * 2 + 3; //vertical space between nodes
    public static horNodeSpace = this.nodeBaseSize * 2 + 2; //horitonzal space between nodes
    public static marginWithinTree = this.nodeBaseSize * 2; //margin between the trees

    public static fontSizeRepAmount = 3; //Base font size for the number that tells how much is represented
}

export function setNodeBaseSize(size: number) {
    this.nodeBaseSize = size;
}


export function setVizSizes(nodeSize: number) {
    vars.nodeBaseSize = nodeSize;
    vars.linkBaseSize = nodeSize / 2; //constant link size depending on node size
    vars.verNodeSpace = nodeSize * 2 + 3; //Vertical space between nodes. *2 as this is the diamater of a node. 
    vars.horNodeSpace = nodeSize * 2 + 2; // Horizontal space between nodes. *2 as this is the diamater of a node.
    vars.marginWithinTree = nodeSize * 2; //Makes sure the tree doesn't get clipped
    // fontSizeRepAmount = nodeSize * 2; //Base font size for the number that tells how much is represented
    vars.fontSizeRepAmount = 3;
}