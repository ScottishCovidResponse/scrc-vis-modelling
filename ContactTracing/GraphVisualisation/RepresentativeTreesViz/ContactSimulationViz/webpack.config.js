module.exports = {
    mode: 'development',
    module: {
        rules: [{
                test: /\.ts$/,
                loader: 'babel-loader',
            },
            {
                test: [/\.js$/],
                exclude: /node_modules/,
                use: {
                    //Without additional settings, this will reference .babelrc
                    loader: 'babel-loader'
                }
            }
        ]
    },
    resolve: {
        extensions: ['.ts', '.js']
    },
    devtool: 'source-map'
}