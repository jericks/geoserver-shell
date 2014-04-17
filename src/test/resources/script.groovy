import geoscript.geom.Geometry

title = 'Buffer'
description = 'Buffers a geometry'

inputs = [
    geom: [name: 'geom', title: 'The geometry to buffer', type: Geometry.class],
    distance: [name: 'distance', title: 'The buffer distance', type: Double.class]
]

outputs = [
    result: [name: 'result', title: 'The buffered geometry', type: Geometry.class]
]

def run(input) {
    [result: input.geom.buffer(input.distance as double)]
}