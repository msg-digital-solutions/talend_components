//============================================================================
//
// Copyright (C) 2006-2023 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
//============================================================================

package org.talend.components.adapter.beam.transform;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

/**
 * Transformation to turn a {@link PCollection} of any type into a {@link PCollection} of {@link IndexedRecord}s.
 *
 * This class implements some standard logic about the conversions present in the {@link AvroRegistry}, which can be
 * configured to "know" how to interpret technology-specific types (in the current virtual machine).
 *
 * @param <DatumT> The type of the incoming collection.
 */
public class ConvertToIndexedRecord<DatumT> extends
        PTransform<PCollection<DatumT>, PCollection<IndexedRecord>> {

    private static final Logger LOG = LoggerFactory.getLogger(ConvertToIndexedRecord.class);

    /** Use the {@link #of()} method to create. */
    protected ConvertToIndexedRecord() {
    }

    /**
     * @return an instance of this transformation.
     */
    public static <DatumT> ConvertToIndexedRecord<DatumT> of() {
        return new ConvertToIndexedRecord<DatumT>();
    }

    /**
     * Converts any datum to an {@link IndexedRecord} representation as if it were passed in the transformation. This
     * might be an expensive call, so it should only be used for sampling data (not in a processing-intensive loop).
     *
     * @param datum the datum to convert.
     * @return its representation as an Avro {@link IndexedRecord}.
     */
    public static <DatumT> IndexedRecord convertToAvro(DatumT datum) {
        IndexedRecordConverter<DatumT, IndexedRecord> c = getConverter(datum);
        if (c == null) {
            throw new Pipeline.PipelineExecutionException(
                    new RuntimeException("Cannot convert " + datum.getClass() + " to IndexedRecord."));
        }
        return (IndexedRecord) c.convertToAvro(datum);
    }

    /**
     * Return a converter that can any datum to an {@link IndexedRecord} representation as if it were passed in the
     * transformation. This might be an expensive call, so the converter should be cached if possible.
     *
     * @param datum the datum to convert.
     * @return a converter that can turn it into an Avro {@link IndexedRecord}.
     */
    public static <DatumT> IndexedRecordConverter<DatumT, IndexedRecord> getConverter(DatumT datum) {
        @SuppressWarnings("unchecked")
        IndexedRecordConverter<DatumT, IndexedRecord> converter = (IndexedRecordConverter<DatumT, IndexedRecord>) new AvroRegistry()
                .createIndexedRecordConverter(datum.getClass());
        if (datum instanceof IndexedRecord) {
            converter.setSchema(((IndexedRecord) datum).getSchema());
        }
        if (converter == null) {
            throw new Pipeline.PipelineExecutionException(
                    new RuntimeException("Cannot convert " + datum.getClass() + " to IndexedRecord."));
        }
        return converter;
    }

    @Override
    public PCollection<IndexedRecord> expand(PCollection<DatumT> input) {
        return input.apply(ParDo.of(new DoFn<DatumT, IndexedRecord>() {

            /** The converter is cached for performance. */
            private transient IndexedRecordConverter<? super DatumT, ? extends IndexedRecord> converter;

            @DoFn.ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                DatumT in = c.element();
                if (in == null) {
                    return;
                }
                if (converter == null) {
                    converter = getConverter(in);
                }
                LOG.debug("Converter's schema is {}", converter.getSchema());
                LOG.debug("Process element is {}", in);
                c.output(converter.convertToAvro(in));
            }

        }));
    }
}
