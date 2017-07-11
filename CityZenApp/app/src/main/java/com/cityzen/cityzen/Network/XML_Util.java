package com.cityzen.cityzen.Network;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.util.Map;

/**
 * Created by Valdio Veliu on 11/05/2017.
 */

public class XML_Util {

    public static String createChangesetXmlBody(Map<String, String> tags) throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        if (tags == null) return null;
        /**
         *
         <osm>
         <changeset>
         <tag k="created_by" v="JOSM 1.61"/>
         <tag k="comment" v="Just adding some streetnames"/>
         ...
         </changeset>
         ...
         </osm>
         */
        xmlSerializer.setOutput(writer);
        // start DOCUMENT
        xmlSerializer.startDocument("UTF-8", true);
        // open tag: <osm>
        xmlSerializer.startTag("", "osm");
        // open tag: <changeset>
        xmlSerializer.startTag("", "changeset");

        //create tags
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            xmlSerializer.startTag("", "tag");
            xmlSerializer.attribute("", "k", tag.getKey());
            xmlSerializer.attribute("", "v", tag.getValue());
            xmlSerializer.endTag("", "tag");
        }

        // close tag: </changeset>
        xmlSerializer.endTag("", "changeset");
        // close tag: </osm>
        xmlSerializer.endTag("", "osm");
        // end DOCUMENT
        xmlSerializer.endDocument();

        return writer.toString();
    }

    public static String updateNodeXmlBody(Map<String, String> tags, long nodeId, String changesetId, double lat, double lon, int versionNumber) throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        if (tags == null) return null;
        /**
         *
         <osm>
         <node  id="..." changeset="12" lat="..." lon="..." version="12">
         <tag k="note" v="Just a node"/>
         ...
         </node>
         </osm>
         */

        xmlSerializer.setOutput(writer);
        // start DOCUMENT
        xmlSerializer.startDocument("UTF-8", true);
        // open tag: <osm>
        xmlSerializer.startTag("", "osm");
        // open tag: <changeset>
        xmlSerializer.startTag("", "node");
        xmlSerializer.attribute("", "id", String.valueOf(nodeId));
        xmlSerializer.attribute("", "changeset", changesetId);
        xmlSerializer.attribute("", "lat", String.valueOf(lat));
        xmlSerializer.attribute("", "lon", String.valueOf(lon));
        xmlSerializer.attribute("", "version", String.valueOf(versionNumber));


        //create tags
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            xmlSerializer.startTag("", "tag");
            xmlSerializer.attribute("", "k", tag.getKey());
            xmlSerializer.attribute("", "v", tag.getValue());
            xmlSerializer.endTag("", "tag");
        }

        // close tag: </changeset>
        xmlSerializer.endTag("", "node");
        // close tag: </osm>
        xmlSerializer.endTag("", "osm");
        // end DOCUMENT
        xmlSerializer.endDocument();

        return writer.toString();
    }

    public static String createNodeXmlBody(Map<String, String> tags, String changesetId, double lat, double lon) throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        if (tags == null) return null;
        /**
         *
         <osm>
         <node changeset="12" lat="..." lon="...">
         <tag k="note" v="Just a node"/>
         ...
         </node>
         </osm>
         */

        xmlSerializer.setOutput(writer);
        // start DOCUMENT
        xmlSerializer.startDocument("UTF-8", true);
        // open tag: <osm>
        xmlSerializer.startTag("", "osm");
        // open tag: <changeset>
        xmlSerializer.startTag("", "node");
        xmlSerializer.attribute("", "changeset", changesetId);
        xmlSerializer.attribute("", "lat", String.valueOf(lat));
        xmlSerializer.attribute("", "lon", String.valueOf(lon));

        //create tags
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            xmlSerializer.startTag("", "tag");
            xmlSerializer.attribute("", "k", tag.getKey());
            xmlSerializer.attribute("", "v", tag.getValue());
            xmlSerializer.endTag("", "tag");
        }

        // close tag: </changeset>
        xmlSerializer.endTag("", "node");
        // close tag: </osm>
        xmlSerializer.endTag("", "osm");
        // end DOCUMENT
        xmlSerializer.endDocument();

        return writer.toString();
    }

}
