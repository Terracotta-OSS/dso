/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tc.config.schema;

import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

import com.tc.exception.ImplementMe;
import com.terracottatech.config.Client;
import com.terracottatech.config.Path;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * A mock {@link Client}, for use in tests.
 */
public class MockClient implements Client {

  public MockClient() {
    super();
  }

  @Override
  public String getLogs() {
    throw new ImplementMe();
  }

  @Override
  public Path xgetLogs() {
    throw new ImplementMe();
  }

  @Override
  public boolean isSetLogs() {
    throw new ImplementMe();
  }

  @Override
  public void setLogs(String arg0) {
    throw new ImplementMe();
  }

  @Override
  public void xsetLogs(Path arg0) {
    throw new ImplementMe();
  }

  @Override
  public void unsetLogs() {
    throw new ImplementMe();
  }

  @Override
  public SchemaType schemaType() {
    throw new ImplementMe();
  }

  @Override
  public boolean validate() {
    throw new ImplementMe();
  }

  @Override
  public boolean validate(XmlOptions arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] selectPath(String arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] selectPath(String arg0, XmlOptions arg1) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] execQuery(String arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] execQuery(String arg0, XmlOptions arg1) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject changeType(SchemaType arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject substitute(QName arg0, SchemaType arg1) {
    throw new ImplementMe();
  }

  @Override
  public boolean isNil() {
    throw new ImplementMe();
  }

  @Override
  public void setNil() {
    throw new ImplementMe();

  }

  @Override
  public boolean isImmutable() {
    throw new ImplementMe();
  }

  @Override
  public XmlObject set(XmlObject arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject copy() {
    throw new ImplementMe();
  }

  @Override
  public XmlObject copy(XmlOptions xmlOptions) { return null; }

  @Override
  public boolean valueEquals(XmlObject arg0) {
    throw new ImplementMe();
  }

  @Override
  public int valueHashCode() {
    throw new ImplementMe();
  }

  @Override
  public int compareTo(Object arg0) {
    throw new ImplementMe();
  }

  @Override
  public int compareValue(XmlObject arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] selectChildren(QName arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] selectChildren(String arg0, String arg1) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] selectChildren(QNameSet arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject selectAttribute(QName arg0) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject selectAttribute(String arg0, String arg1) {
    throw new ImplementMe();
  }

  @Override
  public XmlObject[] selectAttributes(QNameSet arg0) {
    throw new ImplementMe();
  }

  @Override
  public Object monitor() {
    throw new ImplementMe();
  }

  @Override
  public XmlDocumentProperties documentProperties() {
    throw new ImplementMe();
  }

  @Override
  public XmlCursor newCursor() {
    throw new ImplementMe();
  }

  @Override
  public XMLInputStream newXMLInputStream() {
    throw new ImplementMe();
  }

  @Override
  public XMLStreamReader newXMLStreamReader() {
    throw new ImplementMe();
  }

  @Override
  public String xmlText() {
    throw new ImplementMe();
  }

  @Override
  public InputStream newInputStream() {
    throw new ImplementMe();
  }

  @Override
  public Reader newReader() {
    throw new ImplementMe();
  }

  @Override
  public Node newDomNode() {
    throw new ImplementMe();
  }

  @Override
  public Node getDomNode() {
    throw new ImplementMe();
  }

  @Override
  public void save(ContentHandler arg0, LexicalHandler arg1) {
    throw new ImplementMe();
  }

  @Override
  public void save(File arg0) {
    throw new ImplementMe();
  }

  @Override
  public void save(OutputStream arg0) {
    throw new ImplementMe();
  }

  @Override
  public void save(Writer arg0) {
    throw new ImplementMe();
  }

  @Override
  public XMLInputStream newXMLInputStream(XmlOptions arg0) {
    throw new ImplementMe();
  }

  @Override
  public XMLStreamReader newXMLStreamReader(XmlOptions arg0) {
    throw new ImplementMe();
  }

  @Override
  public String xmlText(XmlOptions arg0) {
    throw new ImplementMe();
  }

  @Override
  public InputStream newInputStream(XmlOptions arg0) {
    throw new ImplementMe();
  }

  @Override
  public Reader newReader(XmlOptions arg0) {
    throw new ImplementMe();
  }

  @Override
  public Node newDomNode(XmlOptions arg0) {
    throw new ImplementMe();
  }

  @Override
  public void save(ContentHandler arg0, LexicalHandler arg1, XmlOptions arg2) {
    throw new ImplementMe();
  }

  @Override
  public void save(File arg0, XmlOptions arg1) {
    throw new ImplementMe();
  }

  @Override
  public void save(OutputStream arg0, XmlOptions arg1) {
    throw new ImplementMe();
  }

  @Override
  public void save(Writer arg0, XmlOptions arg1) {
    throw new ImplementMe();
  }

  @Override
  public void dump() {
    throw new ImplementMe();
  }

}
