<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" version="1.0.0">
	<sld:NamedLayer>
		<sld:Name>polygon</sld:Name>
		<sld:UserStyle>
			<sld:Name>polygon</sld:Name>
			<sld:Title>Default Polygon</sld:Title>
			<sld:Abstract>A sample style that draws a polygon</sld:Abstract>
			<sld:FeatureTypeStyle>
				<sld:Name>name</sld:Name>
				<sld:Rule>
					<sld:Name>rule1</sld:Name>
					<sld:Title>Gray Polygon with Black Outline</sld:Title>
					<sld:Abstract>A polygon with a gray fill and a 1 pixel black outline</sld:Abstract>
					<sld:PolygonSymbolizer>
						<sld:Fill>
							<sld:CssParameter name="fill">#AAAAAA</sld:CssParameter>
						</sld:Fill>
						<sld:Stroke/>
					</sld:PolygonSymbolizer>
				</sld:Rule>
			</sld:FeatureTypeStyle>
		</sld:UserStyle>
	</sld:NamedLayer>
</sld:StyledLayerDescriptor>
