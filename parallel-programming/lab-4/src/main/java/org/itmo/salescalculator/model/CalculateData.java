package org.itmo.salescalculator.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.hadoop.io.Writable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalculateData implements Writable {

  private double revenue;
  private int quantity;

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    dataOutput.writeDouble(revenue);
    dataOutput.writeInt(quantity);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    revenue = dataInput.readDouble();
    quantity = dataInput.readInt();
  }
}
