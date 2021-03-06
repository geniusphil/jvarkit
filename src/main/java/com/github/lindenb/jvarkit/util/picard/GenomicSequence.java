package com.github.lindenb.jvarkit.util.picard;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.SAMSequenceRecord;

import com.github.lindenb.jvarkit.lang.AbstractCharSequence;
import com.github.lindenb.jvarkit.util.bio.ChromosomeSequence;

/**
 * 
 * implementation of java.lang.CharSequence for a given
 * chromosome of a picard IndexedFastaSequenceFile
 *
 */
public class GenomicSequence
	extends AbstractCharSequence
	implements ChromosomeSequence
	{
	private IndexedFastaSequenceFile indexedFastaSequenceFile;
	private SAMSequenceRecord samSequenceRecord;
	private byte buffer[]=null;
	private int buffer_pos=-1;
	private int half_buffer_capacity=1000000;
	public GenomicSequence(IndexedFastaSequenceFile indexedFastaSequenceFile ,String chrom)
		{	
		this.indexedFastaSequenceFile=indexedFastaSequenceFile;
		if(this.indexedFastaSequenceFile==null) throw new NullPointerException("IndexedFastaSequenceFile is null");
		if(this.indexedFastaSequenceFile.getSequenceDictionary()==null) throw new NullPointerException("no sequence dictionary in the reference. Use picard to index the sequence.");
		this.samSequenceRecord=this.indexedFastaSequenceFile.getSequenceDictionary().getSequence(chrom);
		if(this.samSequenceRecord==null) throw new IllegalArgumentException("not chromosome "+chrom+" in reference.");
		}
	
	public SAMSequenceRecord getSAMSequenceRecord()
		{
		return samSequenceRecord;
		}
	
	@Override
	public int hashCode()
		{
		return getSAMSequenceRecord().hashCode();
		}
	
	/** get the chromosome name of that genomic sequence */
	@Override
	public String getChrom()
		{
		return getSAMSequenceRecord().getSequenceName();
		}
	
	@Override
	public int length()
		{
		return getSAMSequenceRecord().getSequenceLength();
		}
	
	@Override
	public char charAt(int index0)
		{
		if(index0 >= length())
			{
			throw new IndexOutOfBoundsException("index:"+index0);
			}
		if(buffer!=null && index0>=buffer_pos && index0-buffer_pos < buffer.length)
			{
			return (char)buffer[index0-buffer_pos];
			}
		int minStart=Math.max(0, index0-half_buffer_capacity);
		int maxEnd=Math.min(minStart+2*half_buffer_capacity,this.length());
		this.buffer=this.indexedFastaSequenceFile.getSubsequenceAt(
				getChrom(),
				minStart+1,
				maxEnd).getBases();
		this.buffer_pos=minStart;
		return (char)buffer[index0-minStart];
		}
	}
